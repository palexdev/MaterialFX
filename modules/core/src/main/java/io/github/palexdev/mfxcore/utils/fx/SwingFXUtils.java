/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.utils.fx;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.nio.IntBuffer;

import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/// Class copied from javafx.embed.swing to avoid adding the Swing module.
public class SwingFXUtils {

    private SwingFXUtils() {
    } // no instances

    /// Snapshots the specified [BufferedImage] and stores a copy of
    /// its pixels into a JavaFX [Image] object, creating a new
    /// object if needed.
    /// The returned `Image` will be a static snapshot of the state
    /// of the pixels in the `BufferedImage` at the time the method
    /// completes.  Further changes to the `BufferedImage` will not
    /// be reflected in the `Image`.
    ///
    /// The optional JavaFX [WritableImage] parameter may be reused
    /// to store the copy of the pixels.
    /// A new `Image` will be created if the supplied object is null,
    /// is too small or of a type which the image pixels cannot be easily
    /// converted into.
    ///
    /// @param bimg the `BufferedImage` object to be converted
    /// @param wimg an optional `WritableImage` object that can be used to store the returned pixel data
    /// @return an `Image` object representing a snapshot of the
    /// current pixels in the `BufferedImage`.
    /// @since JavaFX 2.2
    public static WritableImage toFXImage(BufferedImage bimg, WritableImage wimg) {
        int bw = bimg.getWidth();
        int bh = bimg.getHeight();
        switch (bimg.getType()) {
            case BufferedImage.TYPE_INT_ARGB:
            case BufferedImage.TYPE_INT_ARGB_PRE:
                break;
            default:
                BufferedImage converted =
                    new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB_PRE);
                Graphics2D g2d = converted.createGraphics();
                g2d.drawImage(bimg, 0, 0, null);
                g2d.dispose();
                bimg = converted;
                break;
        }
        // assert(bimg.getType == TYPE_INT_ARGB[_PRE]);
        if (wimg != null) {
            int iw = (int) wimg.getWidth();
            int ih = (int) wimg.getHeight();
            if (iw < bw || ih < bh) {
                wimg = null;
            } else if (bw < iw || bh < ih) {
                int[] empty = new int[iw];
                PixelWriter pw = wimg.getPixelWriter();
                PixelFormat<IntBuffer> pf = PixelFormat.getIntArgbPreInstance();
                if (bw < iw) {
                    pw.setPixels(bw, 0, iw - bw, bh, pf, empty, 0, 0);
                }
                if (bh < ih) {
                    pw.setPixels(0, bh, iw, ih - bh, pf, empty, 0, 0);
                }
            }
        }
        if (wimg == null) {
            wimg = new WritableImage(bw, bh);
        }
        PixelWriter pw = wimg.getPixelWriter();
        DataBufferInt db = (DataBufferInt) bimg.getRaster().getDataBuffer();
        int[] data = db.getData();
        int offset = bimg.getRaster().getDataBuffer().getOffset();
        int scan = 0;
        SampleModel sm = bimg.getRaster().getSampleModel();
        if (sm instanceof SinglePixelPackedSampleModel) {
            scan = ((SinglePixelPackedSampleModel) sm).getScanlineStride();
        }

        PixelFormat<IntBuffer> pf = (bimg.isAlphaPremultiplied() ?
            PixelFormat.getIntArgbPreInstance() :
            PixelFormat.getIntArgbInstance());
        pw.setPixels(0, 0, bw, bh, pf, data, offset, scan);
        return wimg;
    }

    /// Determine the optimal BufferedImage type to use for the specified
    /// `fxFormat` allowing for the specified `bimg` to be used
    /// as a potential default storage space if it is not null and is compatible.
    ///
    /// @param fxFormat the PixelFormat of the source FX Image
    /// @param bimg an optional existing `BufferedImage` to be used for storage if it is compatible, or null
    static int
    getBestBufferedImageType(PixelFormat<?> fxFormat, BufferedImage bimg,
                             boolean isOpaque) {
        if (bimg != null) {
            int bimgType = bimg.getType();
            if (bimgType == BufferedImage.TYPE_INT_ARGB ||
                bimgType == BufferedImage.TYPE_INT_ARGB_PRE ||
                (isOpaque &&
                 (bimgType == BufferedImage.TYPE_INT_BGR ||
                  bimgType == BufferedImage.TYPE_INT_RGB))) {
                // We will allow the caller to give us a BufferedImage
                // that has an alpha channel, but we might not otherwise
                // construct one ourselves.
                // We will also allow them to choose their own premultiply
                // type which may not match the image.
                // If left to our own devices we might choose a more specific
                // format as indicated by the choices below.
                return bimgType;
            }
        }
        return switch (fxFormat.getType()) {
            default -> BufferedImage.TYPE_INT_ARGB_PRE;
            case BYTE_BGRA, INT_ARGB -> BufferedImage.TYPE_INT_ARGB;
            case BYTE_RGB -> BufferedImage.TYPE_INT_RGB;
            case BYTE_INDEXED -> (fxFormat.isPremultiplied()
                ? BufferedImage.TYPE_INT_ARGB_PRE
                : BufferedImage.TYPE_INT_ARGB);
        };
    }

    /// Determine the appropriate [WritablePixelFormat] type that can
    /// be used to transfer data into the indicated BufferedImage.
    ///
    /// @param bimg the BufferedImage that will be used as a destination for a `PixelReader<IntBuffer>#getPixels()` operation.
    private static WritablePixelFormat<IntBuffer>
    getAssociatedPixelFormat(BufferedImage bimg) {
        // Should not happen...
        return switch (bimg.getType()) {
            // We lie here for xRGB, but we vetted that the src data was opaque
            // so we can ignore the alpha.  We use ArgbPre instead of Argb
            // just to get a loop that does not have divides in it if the
            // PixelReader happens to not know the data is opaque.
            case BufferedImage.TYPE_INT_RGB, BufferedImage.TYPE_INT_ARGB_PRE -> PixelFormat.getIntArgbPreInstance();
            case BufferedImage.TYPE_INT_ARGB -> PixelFormat.getIntArgbInstance();
            default -> throw new InternalError("Failed to validate BufferedImage type");
        };
    }

    private static boolean checkFXImageOpaque(PixelReader pr, int iw, int ih) {
        for (int x = 0; x < iw; x++) {
            for (int y = 0; y < ih; y++) {
                Color color = pr.getColor(x, y);
                if (color.getOpacity() != 1.0) {
                    return false;
                }
            }
        }
        return true;
    }

    /// Snapshots the specified JavaFX [Image] object and stores a
    /// copy of its pixels into a [BufferedImage] object, creating
    /// a new object if needed.
    /// The method will only convert a JavaFX `Image` that is readable
    /// as per the conditions on the [Image#getPixelReader()] method.
    ///
    /// If the `Image` is not readable, as determined by its
    /// `getPixelReader()` method, then this method will return null.
    /// If the `Image` is a writable, or other dynamic image, then
    /// the `BufferedImage` will only be set to the current state of
    /// the pixels in the image as determined by its [PixelReader].
    /// Further changes to the pixels of the `Image` will not be
    /// reflected in the returned `BufferedImage`.
    ///
    /// The optional `BufferedImage` parameter may be reused to store
    /// the copy of the pixels.
    /// A new `BufferedImage` will be created if the supplied object
    /// is null, is too small or of a type which the image pixels cannot
    /// be easily converted into.
    ///
    /// @param img the JavaFX `Image` to be converted
    /// @param bimg an optional `BufferedImage` object that may be used to store the returned pixel data
    /// @return a `BufferedImage` containing a snapshot of the JavaFX
    /// `Image`, or null if the `Image` is not readable.
    /// @since JavaFX 2.2
    public static BufferedImage fromFXImage(Image img, BufferedImage bimg) {
        PixelReader pr = img.getPixelReader();
        if (pr == null) {
            return null;
        }
        int iw = (int) img.getWidth();
        int ih = (int) img.getHeight();
        PixelFormat<?> fxFormat = pr.getPixelFormat();
        boolean srcPixelsAreOpaque = false;
        switch (fxFormat.getType()) {
            case INT_ARGB_PRE:
            case INT_ARGB:
            case BYTE_BGRA_PRE:
            case BYTE_BGRA:
                // Check fx image opacity only if
                // supplied BufferedImage is without alpha channel
                if (bimg != null &&
                    (bimg.getType() == BufferedImage.TYPE_INT_BGR ||
                     bimg.getType() == BufferedImage.TYPE_INT_RGB)) {
                    srcPixelsAreOpaque = checkFXImageOpaque(pr, iw, ih);
                }
                break;
            case BYTE_RGB:
                srcPixelsAreOpaque = true;
                break;
        }
        int prefBimgType = getBestBufferedImageType(pr.getPixelFormat(), bimg, srcPixelsAreOpaque);
        if (bimg != null) {
            int bw = bimg.getWidth();
            int bh = bimg.getHeight();
            if (bw < iw || bh < ih || bimg.getType() != prefBimgType) {
                bimg = null;
            } else if (iw < bw || ih < bh) {
                Graphics2D g2d = bimg.createGraphics();
                g2d.setComposite(AlphaComposite.Clear);
                g2d.fillRect(0, 0, bw, bh);
                g2d.dispose();
            }
        }
        if (bimg == null) {
            bimg = new BufferedImage(iw, ih, prefBimgType);
        }
        DataBufferInt db = (DataBufferInt) bimg.getRaster().getDataBuffer();
        int[] data = db.getData();
        int offset = bimg.getRaster().getDataBuffer().getOffset();
        int scan = 0;
        SampleModel sm = bimg.getRaster().getSampleModel();
        if (sm instanceof SinglePixelPackedSampleModel) {
            scan = ((SinglePixelPackedSampleModel) sm).getScanlineStride();
        }

        WritablePixelFormat<IntBuffer> pf = getAssociatedPixelFormat(bimg);
        pr.getPixels(0, 0, iw, ih, pf, data, offset, scan);
        return bimg;
    }
}
