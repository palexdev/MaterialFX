/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.materialfx.theming;

import io.github.palexdev.materialfx.theming.base.Theme;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Utility class to deploy {@link Theme}'s resources from the Jar/App to the filesystem.
 * <p>
 * The base path for all deployments is at the OS' temp directory {@code System.getProperty("java.io.tmpdir")} in a
 * subdirectory named "themes-assets".
 * <p></p>
 * The deployment will always occur replacing any existing asset, this way we ensure that all assets are present,
 * unmodified/uncorrupted, while also avoiding costly checks on the filesystem.
 * <p></p>
 * Deployments are stored in a nested map of type: [String, Map[String, Path]].
 * <p>
 * The first key is given by {@link Theme#deployName()}, and allows to check which themes have already been deployed.
 * Keep in mind, that checks on which themes are deployed occur only on this cache in memory, for the same reason above,
 * we void checks on the filesystem.
 * <p>
 * The internal map allows to associate each file in the zip to their path on the filesystem. The only note here is about
 * the key of these maps. The key is the path of the file in the zip, for example:
 * <pre>
 * {@code
 * // Consider this zip file
 * assets.zip
 *  - assets
 *    - images
 *      - image.png
 *    - res
 *      - res.tmp
 *  - root.res
 *
 *  // As already said the base path is TMP_DIR/themes-assets
 *  // The structure on the disk will be as follows
 *  TMP_DIR/assets
 *  TMP_DIR/assets/images
 *  TMP_DIR/assets/images/image.png
 *  TMP_DIR/assets/res
 *  TMP_DIR/assets/res/res.tmp
 *  TMP_DIR/root.res
 *
 *  // The structure in the cache map will be as follows
 *  TMP_DIR/assets
 *  TMP_DIR/assets/images
 *  "assets/images/image.png" -> TMP_DIR/assets/images/image.png
 *  TMP_DIR/assets/res
 *  "assets/res/res.tmp" -> TMP_DIR/assets/res/res.tmp
 *  "root.res" -> TMP_DIR/root.res
 *  // Note that only files are stored in cache!
 * }
 * </pre>
 * <p></p>
 * Last but not least, there are methods to also delete any deployed resource: {@link #clean(Theme)} and {@link #cleanAll()}.
 */
public class Deployer {
	//================================================================================
	// Singleton
	//================================================================================
	private static final Deployer instance = new Deployer();

	public static Deployer instance() {
		return instance;
	}

	//================================================================================
	// Properties
	//================================================================================
	private final Map<String, Map<String, Path>> cache = new HashMap<>();
	private final Path tmpDir = Path.of(System.getProperty("java.io.tmpdir"), "themes-assets");

	//================================================================================
	// Constructors
	//================================================================================
	private Deployer() {}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Retrieves the given {@link Theme}'s assets using {@link Theme#assets()}, if the returned stream is not null
	 * and is a valid zip file, extracts its contents at: {@code System.getProperty("java.io.tmpdir")/themes-assets}.
	 * <p>
	 * The paths in the zip are preserved.
	 * <p></p>
	 * At the end the zip file is deleted.
	 */
	public void deploy(Theme theme) throws Exception {
		if (!Files.isDirectory(tmpDir)) Files.createDirectories(tmpDir);

		Path zipPath = null;
		try (InputStream in = theme.assets()) {
			if (in == null) return;
			Path destDir = tmpDir.resolve(theme.deployName());
			if (!Files.isDirectory(destDir)) Files.createDirectories(destDir);

			// Copy zip to file system
			zipPath = destDir.resolve("assets.zip");
			OutputStream out = Files.newOutputStream(zipPath);
			in.transferTo(out);

			// Unzip
			try (ZipFile zf = new ZipFile(zipPath.toFile())) {
				List<? extends ZipEntry> entries = zf.stream().collect(Collectors.toList());
				for (ZipEntry entry : entries) {
					unzip(theme, zf, entry, destDir);
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (zipPath != null) Files.delete(zipPath);
		}
	}

	/**
	 * If the given {@link Theme} has already been deployed before, removes its entry from the cache map,
	 * and for each stored {@link Path} attempts their removal.
	 *
	 * @return true if the operation was successful or the cache didn't contain the entry, false otherwise
	 */
	public boolean clean(Theme theme) {
		Map<String, Path> assets = cache.remove(theme.deployName());
		if (assets == null) return true;
		try {
			for (Path path : assets.values()) {
				delete(path);
			}
			return true;
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * Removes all the deployed resources on the filesystem by deleting the temp directory, then clears the cache.
	 *
	 * @return true if the operation was successful, false otherwise
	 */
	public boolean cleanAll() {
		try {
			if (Files.isDirectory(tmpDir)) delete(tmpDir);
			cache.clear();
			return true;
		} catch (IOException ignored) {
			return false;
		}
	}

	/**
	 * @return the Map containing the deployed Paths on the filesystem by their path into the zip
	 */
	public Map<String, Path> getDeployed(Theme theme) {
		return cache.get(theme.deployName());
	}

	/**
	 * Responsible for copying resources from the zip file to the filesystem as well as building the cache.
	 */
	private void unzip(Theme theme, ZipFile zf, ZipEntry entry, Path destDir) throws IOException {
		String name = entry.getName();
		Path target = destDir.resolve(name);
		if (entry.isDirectory()) {
			Files.createDirectories(target);
		} else {
			try (InputStream in = zf.getInputStream(entry)) {
				Files.copy(in, target, REPLACE_EXISTING);
				Map<String, Path> themeCache = cache.computeIfAbsent(theme.deployName(), t -> new HashMap<>());
				themeCache.put(name, target);
			}
		}
	}

	/**
	 * Utility method to delete a path whether it is a file or directory, in other words, what Java should have already had
	 * in their java.io/java.nio mess.
	 */
	private void delete(Path path) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<>() {
			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(final Path dir, final IOException e) throws IOException {
				Files.delete(dir);
				return CONTINUE;
			}
		});
	}
}
