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

package io.github.palexdev.mfxresources.fonts.fontawesome;

import io.github.palexdev.mfxresources.fonts.IconDescriptor;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.utils.EnumUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Enumerator class for FontAwesomeBrands icons. (Count: 465)
 */
public enum FontAwesomeBrands implements IconDescriptor {
	GROUP("fab-group", '\uE900'),
	PX("fab-px", '\uE901'),
	ACCESSIBLE_ICON("fab-accessible-icon", '\uE902'),
	ACCUSOFT("fab-accusoft", '\uE903'),
	ADN("fab-adn", '\uE904'),
	ADVERSAL("fab-adversal", '\uE905'),
	AFFILIATETHEME("fab-affiliatetheme", '\uE906'),
	AIRBNB("fab-airbnb", '\uE907'),
	ALGOLIA("fab-algolia", '\uE908'),
	ALIPAY("fab-alipay", '\uE909'),
	AMAZON("fab-amazon", '\uE90A'),
	AMAZON_PAY("fab-amazon-pay", '\uE90B'),
	AMILIA("fab-amilia", '\uE90C'),
	ANDROID("fab-android", '\uE90D'),
	ANGELLIST("fab-angellist", '\uE90E'),
	ANGRYCREATIVE("fab-angrycreative", '\uE90F'),
	ANGULAR("fab-angular", '\uE910'),
	APPER("fab-apper", '\uE911'),
	APPLE("fab-apple", '\uE912'),
	APPLE_PAY("fab-apple-pay", '\uE913'),
	APP_STORE("fab-app-store", '\uE914'),
	APP_STORE_IOS("fab-app-store-ios", '\uE915'),
	ARTSTATION("fab-artstation", '\uE916'),
	ASYMMETRIK("fab-asymmetrik", '\uE917'),
	ATLASSIAN("fab-atlassian", '\uE918'),
	AUDIBLE("fab-audible", '\uE919'),
	AUTOPREFIXER("fab-autoprefixer", '\uE91A'),
	AVIANEX("fab-avianex", '\uE91B'),
	AVIATO("fab-aviato", '\uE91C'),
	AWS("fab-aws", '\uE91D'),
	BANDCAMP("fab-bandcamp", '\uE91E'),
	BATTLE_NET("fab-battle-net", '\uE91F'),
	BEHANCE("fab-behance", '\uE920'),
	BILIBILI("fab-bilibili", '\uE921'),
	BIMOBJECT("fab-bimobject", '\uE922'),
	BITBUCKET("fab-bitbucket", '\uE923'),
	BITCOIN("fab-bitcoin", '\uE924'),
	BITY("fab-bity", '\uE925'),
	BLACKBERRY("fab-blackberry", '\uE926'),
	BLACK_TIE("fab-black-tie", '\uE927'),
	BLOGGER("fab-blogger", '\uE928'),
	BLOGGER_B("fab-blogger-b", '\uE929'),
	BLUETOOTH("fab-bluetooth", '\uE92A'),
	BLUETOOTH_B("fab-bluetooth-b", '\uE92B'),
	BOOTSTRAP("fab-bootstrap", '\uE92C'),
	BOTS("fab-bots", '\uE92D'),
	BTC("fab-btc", '\uE92E'),
	BUFFER("fab-buffer", '\uE92F'),
	BUROMOBELEXPERTE("fab-buromobelexperte", '\uE930'),
	BUY_N_LARGE("fab-buy-n-large", '\uE931'),
	BUYSELLADS("fab-buysellads", '\uE932'),
	CANADIAN_MAPLE_LEAF("fab-canadian-maple-leaf", '\uE933'),
	CC_AMAZON_PAY("fab-cc-amazon-pay", '\uE934'),
	CC_AMEX("fab-cc-amex", '\uE935'),
	CC_APPLE_PAY("fab-cc-apple-pay", '\uE936'),
	CC_DINERS_CLUB("fab-cc-diners-club", '\uE937'),
	CC_DISCOVER("fab-cc-discover", '\uE938'),
	CC_JCB("fab-cc-jcb", '\uE939'),
	CC_MASTERCARD("fab-cc-mastercard", '\uE93A'),
	CC_PAYPAL("fab-cc-paypal", '\uE93B'),
	CC_STRIPE("fab-cc-stripe", '\uE93C'),
	CC_VISA("fab-cc-visa", '\uE93D'),
	CENTERCODE("fab-centercode", '\uE93E'),
	CENTOS("fab-centos", '\uE93F'),
	CHROME("fab-chrome", '\uE940'),
	CHROMECAST("fab-chromecast", '\uE941'),
	CLOUDFLARE("fab-cloudflare", '\uE942'),
	CLOUDSCALE("fab-cloudscale", '\uE943'),
	CLOUDSMITH("fab-cloudsmith", '\uE944'),
	CLOUDVERSIFY("fab-cloudversify", '\uE945'),
	CMPLID("fab-cmplid", '\uE946'),
	CODEPEN("fab-codepen", '\uE947'),
	CODIEPIE("fab-codiepie", '\uE948'),
	CONFLUENCE("fab-confluence", '\uE949'),
	CONNECTDEVELOP("fab-connectdevelop", '\uE94A'),
	CONTAO("fab-contao", '\uE94B'),
	COTTON_BUREAU("fab-cotton-bureau", '\uE94C'),
	CPANEL("fab-cpanel", '\uE94D'),
	CREATIVE_COMMONS("fab-creative-commons", '\uE94E'),
	CREATIVE_COMMONS_BY("fab-creative-commons-by", '\uE94F'),
	CREATIVE_COMMONS_NC("fab-creative-commons-nc", '\uE950'),
	CREATIVE_COMMONS_NC_EU("fab-creative-commons-nc-eu", '\uE951'),
	CREATIVE_COMMONS_NC_JP("fab-creative-commons-nc-jp", '\uE952'),
	CREATIVE_COMMONS_ND("fab-creative-commons-nd", '\uE953'),
	CREATIVE_COMMONS_PD("fab-creative-commons-pd", '\uE954'),
	CREATIVE_COMMONS_PD_ALT("fab-creative-commons-pd-alt", '\uE955'),
	CREATIVE_COMMONS_REMIX("fab-creative-commons-remix", '\uE956'),
	CREATIVE_COMMONS_SA("fab-creative-commons-sa", '\uE957'),
	CREATIVE_COMMONS_SAMPLING("fab-creative-commons-sampling", '\uE958'),
	CREATIVE_COMMONS_SAMPLING_PLUS("fab-creative-commons-sampling-plus", '\uE959'),
	CREATIVE_COMMONS_SHARE("fab-creative-commons-share", '\uE95A'),
	CREATIVE_COMMONS_ZERO("fab-creative-commons-zero", '\uE95B'),
	CRITICAL_ROLE("fab-critical-role", '\uE95C'),
	CSS3("fab-css3", '\uE95D'),
	CSS3_ALT("fab-css3-alt", '\uE95E'),
	CUTTLEFISH("fab-cuttlefish", '\uE95F'),
	DAILYMOTION("fab-dailymotion", '\uE960'),
	D_AND_D("fab-d-and-d", '\uE961'),
	D_AND_D_BEYOND("fab-d-and-d-beyond", '\uE962'),
	DASHCUBE("fab-dashcube", '\uE963'),
	DEEZER("fab-deezer", '\uE964'),
	DELICIOUS("fab-delicious", '\uE965'),
	DEPLOYDOG("fab-deploydog", '\uE966'),
	DESKPRO("fab-deskpro", '\uE967'),
	DEV("fab-dev", '\uE968'),
	DEVIANTART("fab-deviantart", '\uE969'),
	DHL("fab-dhl", '\uE96A'),
	DIASPORA("fab-diaspora", '\uE96B'),
	DIGG("fab-digg", '\uE96C'),
	DIGITAL_OCEAN("fab-digital-ocean", '\uE96D'),
	DISCORD("fab-discord", '\uE96E'),
	DISCOURSE("fab-discourse", '\uE96F'),
	DOCHUB("fab-dochub", '\uE970'),
	DOCKER("fab-docker", '\uE971'),
	DRAFT2DIGITAL("fab-draft2digital", '\uE972'),
	DRIBBBLE("fab-dribbble", '\uE973'),
	DROPBOX("fab-dropbox", '\uE974'),
	DRUPAL("fab-drupal", '\uE975'),
	DYALOG("fab-dyalog", '\uE976'),
	EARLYBIRDS("fab-earlybirds", '\uE977'),
	EBAY("fab-ebay", '\uE978'),
	EDGE("fab-edge", '\uE979'),
	EDGE_LEGACY("fab-edge-legacy", '\uE97A'),
	ELEMENTOR("fab-elementor", '\uE97B'),
	ELLO("fab-ello", '\uE97C'),
	EMBER("fab-ember", '\uE97D'),
	EMPIRE("fab-empire", '\uE97E'),
	ENVIRA("fab-envira", '\uE97F'),
	ERLANG("fab-erlang", '\uE980'),
	ETHEREUM("fab-ethereum", '\uE981'),
	ETSY("fab-etsy", '\uE982'),
	EVERNOTE("fab-evernote", '\uE983'),
	EXPEDITEDSSL("fab-expeditedssl", '\uE984'),
	FACEBOOK("fab-facebook", '\uE985'),
	FACEBOOK_F("fab-facebook-f", '\uE986'),
	FACEBOOK_MESSENGER("fab-facebook-messenger", '\uE987'),
	FANTASY_FLIGHT_GAMES("fab-fantasy-flight-games", '\uE988'),
	FEDEX("fab-fedex", '\uE989'),
	FEDORA("fab-fedora", '\uE98A'),
	FIGMA("fab-figma", '\uE98B'),
	FIREFOX("fab-firefox", '\uE98C'),
	FIREFOX_BROWSER("fab-firefox-browser", '\uE98D'),
	FIRSTDRAFT("fab-firstdraft", '\uE98E'),
	FIRST_ORDER("fab-first-order", '\uE98F'),
	FIRST_ORDER_ALT("fab-first-order-alt", '\uE990'),
	FLICKR("fab-flickr", '\uE991'),
	FLIPBOARD("fab-flipboard", '\uE992'),
	FLY("fab-fly", '\uE993'),
	FONT_AWESOME("fab-font-awesome", '\uE994'),
	FONTICONS("fab-fonticons", '\uE995'),
	FONTICONS_FI("fab-fonticons-fi", '\uE996'),
	FORT_AWESOME("fab-fort-awesome", '\uE997'),
	FORT_AWESOME_ALT("fab-fort-awesome-alt", '\uE998'),
	FORUMBEE("fab-forumbee", '\uE999'),
	FOURSQUARE("fab-foursquare", '\uE99A'),
	FREEBSD("fab-freebsd", '\uE99B'),
	FREE_CODE_CAMP("fab-free-code-camp", '\uE99C'),
	FULCRUM("fab-fulcrum", '\uE99D'),
	GALACTIC_REPUBLIC("fab-galactic-republic", '\uE99E'),
	GALACTIC_SENATE("fab-galactic-senate", '\uE99F'),
	GET_POCKET("fab-get-pocket", '\uE9A0'),
	GG("fab-gg", '\uE9A1'),
	GG_CIRCLE("fab-gg-circle", '\uE9A2'),
	GIT("fab-git", '\uE9A3'),
	GIT_ALT("fab-git-alt", '\uE9A4'),
	GITHUB("fab-github", '\uE9A5'),
	GITHUB_ALT("fab-github-alt", '\uE9A6'),
	GITKRAKEN("fab-gitkraken", '\uE9A7'),
	GITLAB("fab-gitlab", '\uE9A8'),
	GITTER("fab-gitter", '\uE9A9'),
	GLIDE("fab-glide", '\uE9AA'),
	GLIDE_G("fab-glide-g", '\uE9AB'),
	GOFORE("fab-gofore", '\uE9AC'),
	GOLANG("fab-golang", '\uE9AD'),
	GOODREADS("fab-goodreads", '\uE9AE'),
	GOODREADS_G("fab-goodreads-g", '\uE9AF'),
	GOOGLE("fab-google", '\uE9B0'),
	GOOGLE_DRIVE("fab-google-drive", '\uE9B1'),
	GOOGLE_PAY("fab-google-pay", '\uE9B2'),
	GOOGLE_PLAY("fab-google-play", '\uE9B3'),
	GOOGLE_PLUS("fab-google-plus", '\uE9B4'),
	GOOGLE_PLUS_G("fab-google-plus-g", '\uE9B5'),
	GOOGLE_WALLET("fab-google-wallet", '\uE9B6'),
	GRATIPAY("fab-gratipay", '\uE9B7'),
	GRAV("fab-grav", '\uE9B8'),
	GRIPFIRE("fab-gripfire", '\uE9B9'),
	GRUNT("fab-grunt", '\uE9BA'),
	GUILDED("fab-guilded", '\uE9BB'),
	GULP("fab-gulp", '\uE9BC'),
	HACKER_NEWS("fab-hacker-news", '\uE9BD'),
	HACKERRANK("fab-hackerrank", '\uE9BE'),
	HASHNODE("fab-hashnode", '\uE9BF'),
	HIPS("fab-hips", '\uE9C0'),
	HIRE_A_HELPER("fab-hire-a-helper", '\uE9C1'),
	HIVE("fab-hive", '\uE9C2'),
	HOOLI("fab-hooli", '\uE9C3'),
	HORNBILL("fab-hornbill", '\uE9C4'),
	HOTJAR("fab-hotjar", '\uE9C5'),
	HOUZZ("fab-houzz", '\uE9C6'),
	HTML5("fab-html5", '\uE9C7'),
	HUBSPOT("fab-hubspot", '\uE9C8'),
	IDEAL("fab-ideal", '\uE9C9'),
	IMDB("fab-imdb", '\uE9CA'),
	INSTAGRAM("fab-instagram", '\uE9CB'),
	INSTALOD("fab-instalod", '\uE9CC'),
	INTERCOM("fab-intercom", '\uE9CD'),
	INTERNET_EXPLORER("fab-internet-explorer", '\uE9CE'),
	INVISION("fab-invision", '\uE9CF'),
	IOXHOST("fab-ioxhost", '\uE9D0'),
	ITCH_IO("fab-itch-io", '\uE9D1'),
	ITUNES("fab-itunes", '\uE9D2'),
	ITUNES_NOTE("fab-itunes-note", '\uE9D3'),
	JAVA("fab-java", '\uE9D4'),
	JEDI_ORDER("fab-jedi-order", '\uE9D5'),
	JENKINS("fab-jenkins", '\uE9D6'),
	JIRA("fab-jira", '\uE9D7'),
	JOGET("fab-joget", '\uE9D8'),
	JOOMLA("fab-joomla", '\uE9D9'),
	JS("fab-js", '\uE9DA'),
	JSFIDDLE("fab-jsfiddle", '\uE9DB'),
	KAGGLE("fab-kaggle", '\uE9DC'),
	KEYBASE("fab-keybase", '\uE9DD'),
	KEYCDN("fab-keycdn", '\uE9DE'),
	KICKSTARTER("fab-kickstarter", '\uE9DF'),
	KICKSTARTER_K("fab-kickstarter-k", '\uE9E0'),
	KORVUE("fab-korvue", '\uE9E1'),
	LARAVEL("fab-laravel", '\uE9E2'),
	LASTFM("fab-lastfm", '\uE9E3'),
	LEANPUB("fab-leanpub", '\uE9E4'),
	LESS("fab-less", '\uE9E5'),
	LINE("fab-line", '\uE9E6'),
	LINKEDIN("fab-linkedin", '\uE9E7'),
	LINKEDIN_IN("fab-linkedin-in", '\uE9E8'),
	LINODE("fab-linode", '\uE9E9'),
	LINUX("fab-linux", '\uE9EA'),
	LYFT("fab-lyft", '\uE9EB'),
	MAGENTO("fab-magento", '\uE9EC'),
	MAILCHIMP("fab-mailchimp", '\uE9ED'),
	MANDALORIAN("fab-mandalorian", '\uE9EE'),
	MARKDOWN("fab-markdown", '\uE9EF'),
	MASTODON("fab-mastodon", '\uE9F0'),
	MAXCDN("fab-maxcdn", '\uE9F1'),
	MDB("fab-mdb", '\uE9F2'),
	MEDAPPS("fab-medapps", '\uE9F3'),
	MEDIUM("fab-medium", '\uE9F4'),
	MEDRT("fab-medrt", '\uE9F5'),
	MEETUP("fab-meetup", '\uE9F6'),
	MEGAPORT("fab-megaport", '\uE9F7'),
	MENDELEY("fab-mendeley", '\uE9F8'),
	META("fab-meta", '\uE9F9'),
	MICROBLOG("fab-microblog", '\uE9FA'),
	MICROSOFT("fab-microsoft", '\uE9FB'),
	MIX("fab-mix", '\uE9FC'),
	MIXCLOUD("fab-mixcloud", '\uE9FD'),
	MIXER("fab-mixer", '\uE9FE'),
	MIZUNI("fab-mizuni", '\uE9FF'),
	MODX("fab-modx", '\uEA00'),
	MONERO("fab-monero", '\uEA01'),
	NAPSTER("fab-napster", '\uEA02'),
	NEOS("fab-neos", '\uEA03'),
	NFC_DIRECTIONAL("fab-nfc-directional", '\uEA04'),
	NFC_SYMBOL("fab-nfc-symbol", '\uEA05'),
	NIMBLR("fab-nimblr", '\uEA06'),
	NODE("fab-node", '\uEA07'),
	NODE_JS("fab-node-js", '\uEA08'),
	NPM("fab-npm", '\uEA09'),
	NS8("fab-ns8", '\uEA0A'),
	NUTRITIONIX("fab-nutritionix", '\uEA0B'),
	OCTOPUS_DEPLOY("fab-octopus-deploy", '\uEA0C'),
	ODNOKLASSNIKI("fab-odnoklassniki", '\uEA0D'),
	OLD_REPUBLIC("fab-old-republic", '\uEA0E'),
	OPENCART("fab-opencart", '\uEA0F'),
	OPENID("fab-openid", '\uEA10'),
	OPERA("fab-opera", '\uEA11'),
	OPTIN_MONSTER("fab-optin-monster", '\uEA12'),
	ORCID("fab-orcid", '\uEA13'),
	OSI("fab-osi", '\uEA14'),
	PADLET("fab-padlet", '\uEA15'),
	PAGE4("fab-page4", '\uEA16'),
	PAGELINES("fab-pagelines", '\uEA17'),
	PALFED("fab-palfed", '\uEA18'),
	PATREON("fab-patreon", '\uEA19'),
	PAYPAL("fab-paypal", '\uEA1A'),
	PERBYTE("fab-perbyte", '\uEA1B'),
	PERISCOPE("fab-periscope", '\uEA1C'),
	PHABRICATOR("fab-phabricator", '\uEA1D'),
	PHOENIX_FRAMEWORK("fab-phoenix-framework", '\uEA1E'),
	PHOENIX_SQUADRON("fab-phoenix-squadron", '\uEA1F'),
	PHP("fab-php", '\uEA20'),
	PIED_PIPER("fab-pied-piper", '\uEA21'),
	PIED_PIPER_ALT("fab-pied-piper-alt", '\uEA22'),
	PIED_PIPER_HAT("fab-pied-piper-hat", '\uEA23'),
	PIED_PIPER_PP("fab-pied-piper-pp", '\uEA24'),
	PINTEREST("fab-pinterest", '\uEA25'),
	PINTEREST_P("fab-pinterest-p", '\uEA26'),
	PIX("fab-pix", '\uEA27'),
	PLAYSTATION("fab-playstation", '\uEA28'),
	PRODUCT_HUNT("fab-product-hunt", '\uEA29'),
	PUSHED("fab-pushed", '\uEA2A'),
	PYTHON("fab-python", '\uEA2B'),
	QQ("fab-qq", '\uEA2C'),
	QUINSCAPE("fab-quinscape", '\uEA2D'),
	QUORA("fab-quora", '\uEA2E'),
	RASPBERRY_PI("fab-raspberry-pi", '\uEA2F'),
	RAVELRY("fab-ravelry", '\uEA30'),
	REACT("fab-react", '\uEA31'),
	REACTEUROPE("fab-reacteurope", '\uEA32'),
	README("fab-readme", '\uEA33'),
	REBEL("fab-rebel", '\uEA34'),
	REDDIT("fab-reddit", '\uEA35'),
	REDDIT_ALIEN("fab-reddit-alien", '\uEA36'),
	REDHAT("fab-redhat", '\uEA37'),
	RED_RIVER("fab-red-river", '\uEA38'),
	RENREN("fab-renren", '\uEA39'),
	REPLYD("fab-replyd", '\uEA3A'),
	RESEARCHGATE("fab-researchgate", '\uEA3B'),
	RESOLVING("fab-resolving", '\uEA3C'),
	REV("fab-rev", '\uEA3D'),
	ROCKETCHAT("fab-rocketchat", '\uEA3E'),
	ROCKRMS("fab-rockrms", '\uEA3F'),
	R_PROJECT("fab-r-project", '\uEA40'),
	RUST("fab-rust", '\uEA41'),
	SAFARI("fab-safari", '\uEA42'),
	SALESFORCE("fab-salesforce", '\uEA43'),
	SASS("fab-sass", '\uEA44'),
	SCHLIX("fab-schlix", '\uEA45'),
	SCREENPAL("fab-screenpal", '\uEA46'),
	SCRIBD("fab-scribd", '\uEA47'),
	SEARCHENGIN("fab-searchengin", '\uEA48'),
	SELLCAST("fab-sellcast", '\uEA49'),
	SELLSY("fab-sellsy", '\uEA4A'),
	SERVICESTACK("fab-servicestack", '\uEA4B'),
	SHIRTSINBULK("fab-shirtsinbulk", '\uEA4C'),
	SHOPIFY("fab-shopify", '\uEA4D'),
	SHOPWARE("fab-shopware", '\uEA4E'),
	SIMPLYBUILT("fab-simplybuilt", '\uEA4F'),
	SISTRIX("fab-sistrix", '\uEA50'),
	SITH("fab-sith", '\uEA51'),
	SITROX("fab-sitrox", '\uEA52'),
	SKETCH("fab-sketch", '\uEA53'),
	SKYATLAS("fab-skyatlas", '\uEA54'),
	SKYPE("fab-skype", '\uEA55'),
	SLACK("fab-slack", '\uEA56'),
	SLIDESHARE("fab-slideshare", '\uEA57'),
	SNAPCHAT("fab-snapchat", '\uEA58'),
	SOUNDCLOUD("fab-soundcloud", '\uEA59'),
	SOURCETREE("fab-sourcetree", '\uEA5A'),
	SPACE_AWESOME("fab-space-awesome", '\uEA5B'),
	SPEAKAP("fab-speakap", '\uEA5C'),
	SPEAKER_DECK("fab-speaker-deck", '\uEA5D'),
	SPOTIFY("fab-spotify", '\uEA5E'),
	SQUARE_BEHANCE("fab-square-behance", '\uEA5F'),
	SQUARE_DRIBBBLE("fab-square-dribbble", '\uEA60'),
	SQUARE_FACEBOOK("fab-square-facebook", '\uEA61'),
	SQUARE_FONT_AWESOME("fab-square-font-awesome", '\uEA62'),
	SQUARE_FONT_AWESOME_STROKE("fab-square-font-awesome-stroke", '\uEA63'),
	SQUARE_GIT("fab-square-git", '\uEA64'),
	SQUARE_GITHUB("fab-square-github", '\uEA65'),
	SQUARE_GITLAB("fab-square-gitlab", '\uEA66'),
	SQUARE_GOOGLE_PLUS("fab-square-google-plus", '\uEA67'),
	SQUARE_HACKER_NEWS("fab-square-hacker-news", '\uEA68'),
	SQUARE_INSTAGRAM("fab-square-instagram", '\uEA69'),
	SQUARE_JS("fab-square-js", '\uEA6A'),
	SQUARE_LASTFM("fab-square-lastfm", '\uEA6B'),
	SQUARE_ODNOKLASSNIKI("fab-square-odnoklassniki", '\uEA6C'),
	SQUARE_PIED_PIPER("fab-square-pied-piper", '\uEA6D'),
	SQUARE_PINTEREST("fab-square-pinterest", '\uEA6E'),
	SQUARE_REDDIT("fab-square-reddit", '\uEA6F'),
	SQUARE_SNAPCHAT("fab-square-snapchat", '\uEA70'),
	SQUARESPACE("fab-squarespace", '\uEA71'),
	SQUARE_STEAM("fab-square-steam", '\uEA72'),
	SQUARE_TUMBLR("fab-square-tumblr", '\uEA73'),
	SQUARE_TWITTER("fab-square-twitter", '\uEA74'),
	SQUARE_VIADEO("fab-square-viadeo", '\uEA75'),
	SQUARE_VIMEO("fab-square-vimeo", '\uEA76'),
	SQUARE_WHATSAPP("fab-square-whatsapp", '\uEA77'),
	SQUARE_XING("fab-square-xing", '\uEA78'),
	SQUARE_YOUTUBE("fab-square-youtube", '\uEA79'),
	STACK_EXCHANGE("fab-stack-exchange", '\uEA7A'),
	STACK_OVERFLOW("fab-stack-overflow", '\uEA7B'),
	STACKPATH("fab-stackpath", '\uEA7C'),
	STAYLINKED("fab-staylinked", '\uEA7D'),
	STEAM("fab-steam", '\uEA7E'),
	STEAM_SYMBOL("fab-steam-symbol", '\uEA7F'),
	STICKER_MULE("fab-sticker-mule", '\uEA80'),
	STRAVA("fab-strava", '\uEA81'),
	STRIPE("fab-stripe", '\uEA82'),
	STRIPE_S("fab-stripe-s", '\uEA83'),
	STUDIOVINARI("fab-studiovinari", '\uEA84'),
	STUMBLEUPON("fab-stumbleupon", '\uEA85'),
	STUMBLEUPON_CIRCLE("fab-stumbleupon-circle", '\uEA86'),
	SUPERPOWERS("fab-superpowers", '\uEA87'),
	SUPPLE("fab-supple", '\uEA88'),
	SUSE("fab-suse", '\uEA89'),
	SWIFT("fab-swift", '\uEA8A'),
	SYMFONY("fab-symfony", '\uEA8B'),
	TEAMSPEAK("fab-teamspeak", '\uEA8C'),
	TELEGRAM("fab-telegram", '\uEA8D'),
	TENCENT_WEIBO("fab-tencent-weibo", '\uEA8E'),
	THEMECO("fab-themeco", '\uEA8F'),
	THEMEISLE("fab-themeisle", '\uEA90'),
	THE_RED_YETI("fab-the-red-yeti", '\uEA91'),
	THINK_PEAKS("fab-think-peaks", '\uEA92'),
	TIKTOK("fab-tiktok", '\uEA93'),
	TRADE_FEDERATION("fab-trade-federation", '\uEA94'),
	TRELLO("fab-trello", '\uEA95'),
	TUMBLR("fab-tumblr", '\uEA96'),
	TWITCH("fab-twitch", '\uEA97'),
	TWITTER("fab-twitter", '\uEA98'),
	TYPO3("fab-typo3", '\uEA99'),
	UBER("fab-uber", '\uEA9A'),
	UBUNTU("fab-ubuntu", '\uEA9B'),
	UIKIT("fab-uikit", '\uEA9C'),
	UMBRACO("fab-umbraco", '\uEA9D'),
	UNCHARTED("fab-uncharted", '\uEA9E'),
	UNIREGISTRY("fab-uniregistry", '\uEA9F'),
	UNITY("fab-unity", '\uEAA0'),
	UNSPLASH("fab-unsplash", '\uEAA1'),
	UNTAPPD("fab-untappd", '\uEAA2'),
	UPS("fab-ups", '\uEAA3'),
	USB("fab-usb", '\uEAA4'),
	USPS("fab-usps", '\uEAA5'),
	USSUNNAH("fab-ussunnah", '\uEAA6'),
	VAADIN("fab-vaadin", '\uEAA7'),
	VIACOIN("fab-viacoin", '\uEAA8'),
	VIADEO("fab-viadeo", '\uEAA9'),
	VIBER("fab-viber", '\uEAAA'),
	VIMEO("fab-vimeo", '\uEAAB'),
	VIMEO_V("fab-vimeo-v", '\uEAAC'),
	VINE("fab-vine", '\uEAAD'),
	VK("fab-vk", '\uEAAE'),
	VNV("fab-vnv", '\uEAAF'),
	VUEJS("fab-vuejs", '\uEAB0'),
	WATCHMAN_MONITORING("fab-watchman-monitoring", '\uEAB1'),
	WAZE("fab-waze", '\uEAB2'),
	WEEBLY("fab-weebly", '\uEAB3'),
	WEIBO("fab-weibo", '\uEAB4'),
	WEIXIN("fab-weixin", '\uEAB5'),
	WHATSAPP("fab-whatsapp", '\uEAB6'),
	WHMCS("fab-whmcs", '\uEAB7'),
	WIKIPEDIA_W("fab-wikipedia-w", '\uEAB8'),
	WINDOWS("fab-windows", '\uEAB9'),
	WIRSINDHANDWERK("fab-wirsindhandwerk", '\uEABA'),
	WIX("fab-wix", '\uEABB'),
	WIZARDS_OF_THE_COAST("fab-wizards-of-the-coast", '\uEABC'),
	WODU("fab-wodu", '\uEABD'),
	WOLF_PACK_BATTALION("fab-wolf-pack-battalion", '\uEABE'),
	WORDPRESS("fab-wordpress", '\uEABF'),
	WORDPRESS_SIMPLE("fab-wordpress-simple", '\uEAC0'),
	WPBEGINNER("fab-wpbeginner", '\uEAC1'),
	WPEXPLORER("fab-wpexplorer", '\uEAC2'),
	WPFORMS("fab-wpforms", '\uEAC3'),
	WPRESSR("fab-wpressr", '\uEAC4'),
	XBOX("fab-xbox", '\uEAC5'),
	XING("fab-xing", '\uEAC6'),
	YAHOO("fab-yahoo", '\uEAC7'),
	YAMMER("fab-yammer", '\uEAC8'),
	YANDEX("fab-yandex", '\uEAC9'),
	YANDEX_INTERNATIONAL("fab-yandex-international", '\uEACA'),
	YARN("fab-yarn", '\uEACB'),
	Y_COMBINATOR("fab-y-combinator", '\uEACC'),
	YELP("fab-yelp", '\uEACD'),
	YOAST("fab-yoast", '\uEACE'),
	YOUTUBE("fab-youtube", '\uEACF'),
	ZHIHU("fab-zhihu", '\uEAD0'),
	;

	private static Map<String, Character> cache;
	private final String description;
	private final char code;

	FontAwesomeBrands(String description, char code) {
		this.description = description;
		this.code = code;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public char getCode() {
		return code;
	}

	@Override
	public Map<String, Character> getCache() {
		return cache();
	}

	/**
	 * @return a new {@link MFXFontIcon} with a random {@link IconDescriptor} from this enumeration
	 */
	public static MFXFontIcon random() {
		FontAwesomeBrands desc = EnumUtils.randomEnum(FontAwesomeBrands.class);
		return new MFXFontIcon(desc);
	}

	/**
	 * @return a new {@link MFXFontIcon} with a random {@link IconDescriptor} from this enumeration and the given size
	 */
	public static MFXFontIcon random(double size) {
		return random().setSize(size);
	}

	/**
	 * Converts the given icon description/name to its corresponding unicode character.
	 *
	 * @param desc the icon description/name
	 * @return the icon's unicode character
	 * @throws IllegalArgumentException if no icon with the given description could be found
	 */
	public static char toCode(String desc) {
		return Optional.ofNullable(cache().get(desc))
				.orElseThrow(() -> new IllegalArgumentException("Icon description '" + desc + "' is invalid!"));
	}

	/**
	 * Same as {@link IconDescriptor#getCache()}, allows to retrieve the cache from a static context.
	 */
	public static Map<String, Character> cache() {
		if (cache == null) {
			cache = Arrays.stream(values())
					.collect(Collectors.toUnmodifiableMap(
							FontAwesomeBrands::getDescription,
							FontAwesomeBrands::getCode
					));
		}
		return cache;
	}
}
