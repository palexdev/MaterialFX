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
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * The best way to style a JavaFX application is to use {@link Application#setUserAgentStylesheet(String)} because the
 * styling will be applied everywhere, even in separate windows/scenes. And this is huge for developers that create their
 * own themes. First, it's not needed to track every single window/scene (which means no more worries about
 * dialogs/popups and such not being styled); and second the performance will be much better since the theme will be parsed
 * only once.
 * <p>
 * However, ideally the author of a theme should still use one of the JavaFX's default themes as a base, to make sure that
 * every control is covered, or maybe because his themes are just for his custom controls (coff coff MaterialFX coff).
 * For whatever reason, it's always a good idea to have a base.
 * <p>
 * Unfortunately as of now, JavaFX doesn't have a Theming API which would easily allow to create a single theme from a group
 * of stylesheets. Which means that there is no way to set a user agent stylesheet that can cover all the controls.
 * There IS a proposal for such API <a href=https://github.com/openjdk/jfx/pull/511>#511</a>, but at the time of writing
 * this, it's been closed and I don't think we'll see such API in the near future, as always JavaFX development is so SLOW and
 * clumsy when it comes to new features, it's a shame really.
 * <p></p>
 * This builder is an attempt at supporting such use cases, and while it's true that the goal can be achieved, it doesn't come
 * without any caveats that I will discuss later on.
 * <p>
 * The mechanism is rather simple, you can specify a set of themes with {@link #themes(Theme...)}, these will be merged into
 * a single stylesheet by the {@link #build()} method. The result is a {@link CSSFragment} object that, once converted to
 * a Data URI through {@link CSSFragment#toDataUri()}, can be set as the {@link Application}'s user agent, can be added
 * on a {@link Scene} or on a {@link Parent}. The explicit conversion can be avoided by using one of the convenience
 * method offered by {@link CSSFragment}.
 * <p></p>
 * With the recent improvements, this build is also capable of managing URL resources and @import statements.
 * <p>
 * Now, let's talk about the <b>caveats</b>.
 * <p>
 * Before the final stylesheet can be feed to {@link CSSFragment}, it needs to be processed by
 * {@link Processor#preProcess(Theme, String, boolean)} and then by {@link Processor#postProcess(StringBuilder)}.
 * <p>
 * The {@link Processor} is a naive attempt at reading CSS files, for this reason it expects first and foremost well formatted,
 * uncompressed and beautified CSS files. Even in such conditions, there may be unconsidered/unimplemented cases that could
 * make the process fail. Report any issue, and I'll see if anything can be done to fix it.
 * <p></p>
 * Imports and URL resources are supported only if the {@link Theme}s have been deployed and their assets are now accessible
 * on the filesystem. The processor will attempt to convert any 'local' resource to a path on the disk, and this is
 * another delicate point of the whole process as the attempt may fail for unexpected reasons.
 * <p></p>
 * The whole process can be a bit slow, although considering how big are JavaFX's and MaterialFX's themes, and also
 * considering that now they have to deploy their resources on the disk first, it's really not that bad.
 * On my main PCs I didn't notice any major slowdowns, at max 1s. On my tablet, which by the way, it's an Android tablet
 * on which I installed Windows, so take into account that, I noticed the same average. Mileage may vary!
 * <p>
 * Such operations can be enabled/disabled with: {@link #setResolveAssets(boolean)}, {@link #setDeploy(boolean)}.
 */
public class UserAgentBuilder {
	//================================================================================
	// Properties
	//================================================================================
	private final Set<Theme> themes = new LinkedHashSet<>();
	private boolean resolveAssets = false;
	private boolean deploy = false;
	private boolean debug = false;

	//================================================================================
	// Constructors
	//================================================================================
	public UserAgentBuilder() {
	}

	public static UserAgentBuilder builder() {
		return new UserAgentBuilder();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Allows specifying all the themes that will be merged by {@link #build()}.
	 * <p>
	 * The themes are stored in a {@link LinkedHashSet}, ensuring insertion order and avoiding duplicates.
	 */
	public UserAgentBuilder themes(Theme... themes) {
		Collections.addAll(this.themes, themes);
		return this;
	}

	/**
	 * Allows specifying all the themes that will be merged by {@link #build()}.
	 * <p>
	 * The themes are stored in a {@link LinkedHashSet}, ensuring insertion order and avoiding duplicates.
	 */
	public UserAgentBuilder themes(Collection<? extends Theme> themes) {
		this.themes.addAll(themes);
		return this;
	}

	/**
	 * Iterates over all the themes added through {@link #themes(Theme...)}. If {@link #isDeploy()} has been set to true
	 * the theme is deployed by {@link Theme#deploy()}. Then the data is loaded with {@link #load(Theme)} and pre-processed,
	 * by {@link Processor#preProcess(Theme, String, boolean)}. The processed data is added to a {@link StringBuilder}.
	 * <p>
	 * After all the themes have been processed, the data in the {@link StringBuilder} is post-processed by
	 * {@link Processor#postProcess(StringBuilder)} and finally a new {@link CSSFragment} object is created with the
	 * processed data of the merged stylesheet.
	 */
	public CSSFragment build() {
		StringBuilder sb = new StringBuilder();
		Processor processor = new Processor();
		for (Theme theme : themes) {
			if (isDeploy()) theme.deploy();
			String data = load(theme);
			String preProcessed = processor.preProcess(theme, data, resolveAssets);
			sb.append(preProcessed).append("\n\n");
		}
		String postProcess = processor.postProcess(sb);
		if (isDebug()) saveOnDisk(postProcess);
		return new CSSFragment(postProcess);
	}

	/**
	 * Loads a stylesheet specified by the given {@link Theme}. To achieve this, two streams are used, an {@link InputStream}
	 * which derives from the theme's URL, {@link URL#openStream()}, and a {@link ByteArrayOutputStream}.
	 * <p>
	 * The stylesheet's contents are read from the input stream and wrote to the output stream. At the end, the latter
	 * is converter to a string by using {@link ByteArrayOutputStream#toString(Charset)} and {@link StandardCharsets#UTF_8}.
	 * <p></p>
	 * In case of errors, an empty string is returned, and the exception printed to the stdout.
	 */
	protected String load(Theme theme) {
		try (InputStream is = theme.get().openStream();
			 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[8192];
			for (int length; (length = is.read(buffer)) != -1; ) {
				baos.write(buffer, 0, length);
			}
			return baos.toString(StandardCharsets.UTF_8);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	protected void saveOnDisk(String ua) {
		try {
			Files.writeString(
				Files.createTempFile("uab-output", ".css"),
				ua,
				StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING
			);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return whether the {@link Processor} should attempt at resolving @import rules and URL resources
	 */
	public boolean isResolveAssets() {
		return resolveAssets;
	}

	/**
	 * Sets whether the {@link Processor} should attempt at resolving @import rules and URL resources.
	 */
	public UserAgentBuilder setResolveAssets(boolean resolveAssets) {
		this.resolveAssets = resolveAssets;
		return this;
	}

	/**
	 * @return whether the builder should invoke {@link Theme#deploy()} before the theme data is processed
	 */
	public boolean isDeploy() {
		return deploy;
	}

	/**
	 * Sets whether the builder should invoke {@link Theme#deploy()} before the theme data is processed.
	 */
	public UserAgentBuilder setDeploy(boolean deploy) {
		this.deploy = deploy;
		return this;
	}

	/**
	 * @return whether the build process will generate output on disk for debug purposes
	 * @see #setDebug(boolean)
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * Sets the build process will generate output on disk for debug purposes.
	 * The generated CSS stylesheet is saved in the OS' temp directory.
	 */
	public UserAgentBuilder setDebug(boolean debug) {
		this.debug = debug;
		return this;
	}

	//================================================================================
	// Internal Classes
	//================================================================================

	/**
	 * Responsible for pre-processing the themes fed to {@link UserAgentBuilder}, as well as post-processing the
	 * merged stylesheet produced by {@link UserAgentBuilder#build()} before it's returned as a {@link CSSFragment}.
	 * <p></p>
	 * This is a basic and naive approach at reading a CSS file. The processor expects well-formatted, uncompressed
	 * and beautified stylesheets. Even in such conditions, the process may fail because on unconsidered/unimplemented
	 * cases.
	 * <p></p>
	 * As already said the {@code Processor} splits its job in two phases: pre-process and post-process.
	 * <p>
	 * During the {@code pre-process} phase, it reads each line of the stylesheet and performs the following modifications:
	 * <p> - Removes any comment, single and multiple lines
	 * <p> - Attempts at converting 'relative' @import statements to disk paths, and stores them in a Set
	 * <p> - Attempts at converting 'relative' URL resources to disk paths
	 * <p> - Every other kind of line is added without any modification
	 * <p></p>
	 * During the {@code post-process} phase, @import statements processed and stored before, are added at the top of the
	 * merged stylesheet.
	 */
	private static class Processor {
		enum Type {
			START_COMMENT,
			END_COMMENT,
			IMPORT,
			URL,
			OTHER
		}

		private final Set<String> imports = new LinkedHashSet<>();

		/**
		 * Given a {@link Theme} and its loaded stylesheet in the form of a single String, performs some modifications on
		 * the data.
		 * <p></p>
		 * <p> - Comments are removed.
		 * <p> - Imports are resolved and stored is a Set. The resolve is performed by {@link #resolveImport(Theme, String)}.
		 * If the import resource was found in the deployed resources of the theme (see {@link Deployer}), then the
		 * import directive is converted as follows (without quotes): "@import "file:///PATH_ON_THE_DISK";".
		 * It's super important to add the 'file:///' protocol so that the CSS parser can correctly find the resource
		 * <p> - URLs are resolved by {@link #resolveResource(Theme, String)}. If the resource was found in the deployed
		 * of the theme (see {@link Deployer}), then the URL directive is converted as follows (without quotes):
		 * "url("PATH_ON_THE_DISK");". If the URL points to a network resource, then there's no need to convert it.
		 *
		 * @return the pre-processed theme's data
		 */
		public String preProcess(Theme theme, String data, boolean resolveAssets) {
			String[] lines = data.split("\n");
			StringBuilder sb = new StringBuilder();
			boolean insideComment = false;
			for (String line : lines) {
				if (line.isBlank()) continue;
				Type type = typeOf(line);

				/* Ignore comments */
				if (type == Type.START_COMMENT) {
					if (line.trim().endsWith("*/")) continue; /* One line comment */
					insideComment = true;
					continue;
				}
				if (type == Type.END_COMMENT) {
					insideComment = false;
					continue;
				}
				if (insideComment) continue;

				/* Resolved imports should be stored and added back by the post-processing */
				if (type == Type.IMPORT && resolveAssets) {
					Path path = resolveImport(theme, line);
					if (path == null || !Files.exists(path)) {
						System.err.println("Could not resolve import: " + line);
						continue;
					}
					line = "@import \"file:///" + path.toString().replace("\\", "/") + "\";";
					imports.add(line);
					continue;
				}

				/* Resolve URLs */
				if (type == Type.URL && resolveAssets) {
					if (!isNetworkResource(line)) {
						String[] split = line.split(": ");
						Path path = resolveResource(theme, split[1]);
						if (path == null || !Files.exists(path)) continue;
						line = line.replaceAll("^(\\s+).+", "$1") + split[0] + ": url(" + path + ");";
					}
				}

				if (sb.toString().endsWith("}\n")) sb.append("\n");
				sb.append(line).append("\n");
			}
			return sb.toString();
		}

		/**
		 * Given the pre-processed data as a {@link StringBuilder} adds all the imports stored by
		 * {@link #preProcess(Theme, String, boolean)} at the top.
		 *
		 * @return the post-processed data as a String
		 */
		public String postProcess(StringBuilder data) {
			int offset = 0;
			for (String imp : imports) {
				data.insert(offset, imp + "\n");
				offset += imp.length() + 1;
			}
			return data.toString();
		}

		/**
		 * Responsible for resolving the given CSS @import line to a deployed resource on the disk.
		 */
		private Path resolveImport(Theme theme, String line) {
			String[] split = line.replace("\"", "")
				.replace("'", "")
				.replace(";", "")
				.replace("../", "")
				.split(" ");
			String path = split[1];
			Map<String, Path> deployed = Deployer.instance().getDeployed(theme);
			return deployed.get(path);
		}

		/**
		 * Responsible for resolving the given CSS URL line to a deployed resource on the disk.
		 * <p>
		 * The resource's name is resolved by {@link #getResourceName(String)}.
		 */
		private Path resolveResource(Theme theme, String url) {
			String name = getResourceName(url);
			Map<String, Path> deployed = Deployer.instance().getDeployed(theme);
			return deployed.get(name);
		}

		/**
		 * Polishes the given URL string to get the resource's name.
		 */
		private String getResourceName(String url) {
			return url.replace("url(", "").replace(");", "");
		}

		/**
		 * Checks whether the given URL line is a network resource.
		 * A naive approach that checks whether the string contains "http://" or "https://" or "www.".
		 */
		private boolean isNetworkResource(String line) {
			return line.contains("http://") || line.contains("https://") || line.contains("www.");
		}

		/**
		 * Given the current CSS line being processed, determines its type.
		 */
		private Type typeOf(String line) {
			String trim = line.trim();
			if (trim.startsWith("/*")) return Type.START_COMMENT;
			if (trim.endsWith("*/")) return Type.END_COMMENT;
			if (trim.startsWith("@import")) return Type.IMPORT;
			if (trim.contains("url(")) return Type.URL;
			return Type.OTHER;
		}
	}
}
