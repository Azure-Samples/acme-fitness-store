package com.vmware.acme.simulation;

import java.util.Map;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.AllowList;
import static io.gatling.javaapi.core.CoreDsl.DenyList;
import static io.gatling.javaapi.core.CoreDsl.RawFileBody;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;


public class GuestSimulation extends Simulation {
	private static final String GATEWAY_URL = System.getenv("GATEWAY_URL");

	private HttpProtocolBuilder httpProtocol = http
			.baseUrl(GATEWAY_URL)
			.inferHtmlResources(AllowList(), DenyList())
			.acceptHeader("image/avif,image/webp,*/*")
			.acceptEncodingHeader("gzip, deflate")
			.acceptLanguageHeader("en-US,en;q=0.5")
			.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:93.0) Gecko/20100101 Firefox/93.0");

	private Map<CharSequence, String> headers_0 = Map.ofEntries(
			Map.entry("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8"),
			Map.entry("Sec-Fetch-Dest", "document"),
			Map.entry("Sec-Fetch-Mode", "navigate"),
			Map.entry("Sec-Fetch-Site", "none"),
			Map.entry("Sec-Fetch-User", "?1"),
			Map.entry("Upgrade-Insecure-Requests", "1")
	);

	private Map<CharSequence, String> headers_1 = Map.ofEntries(
			Map.entry("Accept", "application/font-woff2;q=1.0,application/font-woff;q=0.9,*/*;q=0.8"),
			Map.entry("Accept-Encoding", "identity"),
			Map.entry("Sec-Fetch-Dest", "font"),
			Map.entry("Sec-Fetch-Mode", "cors"),
			Map.entry("Sec-Fetch-Site", "same-origin")
	);

	private Map<CharSequence, String> headers_2 = Map.ofEntries(
			Map.entry("Accept", "text/html, */*; q=0.01"),
			Map.entry("Sec-Fetch-Dest", "empty"),
			Map.entry("Sec-Fetch-Mode", "cors"),
			Map.entry("Sec-Fetch-Site", "same-origin"),
			Map.entry("X-Requested-With", "XMLHttpRequest")
	);

	private Map<CharSequence, String> headers_3 = Map.ofEntries(
			Map.entry("Sec-Fetch-Dest", "image"),
			Map.entry("Sec-Fetch-Mode", "no-cors"),
			Map.entry("Sec-Fetch-Site", "same-origin")
	);

	private Map<CharSequence, String> headers_6 = Map.ofEntries(
			Map.entry("Accept", "*/*"),
			Map.entry("Sec-Fetch-Dest", "empty"),
			Map.entry("Sec-Fetch-Mode", "cors"),
			Map.entry("Sec-Fetch-Site", "same-origin"),
			Map.entry("X-Requested-With", "XMLHttpRequest")
	);

	private Map<CharSequence, String> headers_13 = Map.ofEntries(
			Map.entry("Accept", "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01"),
			Map.entry("Sec-Fetch-Dest", "empty"),
			Map.entry("Sec-Fetch-Mode", "cors"),
			Map.entry("Sec-Fetch-Site", "same-origin"),
			Map.entry("X-Requested-With", "XMLHttpRequest")
	);

	private Map<CharSequence, String> headers_15 = Map.ofEntries(
			Map.entry("Accept", "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01"),
			Map.entry("Cache-Control", "max-age=0"),
			Map.entry("If-Modified-Since", "Tue, 01 Jan 1980 00:00:01 GMT"),
			Map.entry("If-None-Match", "W/\"88d-49773873e8\""),
			Map.entry("Sec-Fetch-Dest", "empty"),
			Map.entry("Sec-Fetch-Mode", "cors"),
			Map.entry("Sec-Fetch-Site", "same-origin"),
			Map.entry("X-Requested-With", "XMLHttpRequest")
	);

	private Map<CharSequence, String> headers_20 = Map.ofEntries(
			Map.entry("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8"),
			Map.entry("Sec-Fetch-Dest", "document"),
			Map.entry("Sec-Fetch-Mode", "navigate"),
			Map.entry("Sec-Fetch-Site", "same-origin"),
			Map.entry("Sec-Fetch-User", "?1"),
			Map.entry("Upgrade-Insecure-Requests", "1")
	);

	private Map<CharSequence, String> headers_21 = Map.ofEntries(
			Map.entry("Accept", "text/html, */*; q=0.01"),
			Map.entry("If-Modified-Since", "Tue, 01 Jan 1980 00:00:01 GMT"),
			Map.entry("If-None-Match", "W/\"117f-49773873e8\""),
			Map.entry("Sec-Fetch-Dest", "empty"),
			Map.entry("Sec-Fetch-Mode", "cors"),
			Map.entry("Sec-Fetch-Site", "same-origin"),
			Map.entry("X-Requested-With", "XMLHttpRequest")
	);

	private Map<CharSequence, String> headers_22 = Map.ofEntries(
			Map.entry("Accept", "text/html, */*; q=0.01"),
			Map.entry("If-Modified-Since", "Tue, 01 Jan 1980 00:00:01 GMT"),
			Map.entry("If-None-Match", "W/\"baf-49773873e8\""),
			Map.entry("Sec-Fetch-Dest", "empty"),
			Map.entry("Sec-Fetch-Mode", "cors"),
			Map.entry("Sec-Fetch-Site", "same-origin"),
			Map.entry("X-Requested-With", "XMLHttpRequest")
	);

	private Map<CharSequence, String> headers_23 = Map.ofEntries(
			Map.entry("Accept", "text/html, */*; q=0.01"),
			Map.entry("If-Modified-Since", "Tue, 01 Jan 1980 00:00:01 GMT"),
			Map.entry("If-None-Match", "W/\"aee-49773873e8\""),
			Map.entry("Sec-Fetch-Dest", "empty"),
			Map.entry("Sec-Fetch-Mode", "cors"),
			Map.entry("Sec-Fetch-Site", "same-origin"),
			Map.entry("X-Requested-With", "XMLHttpRequest")
	);

	private Map<CharSequence, String> headers_25 = Map.ofEntries(
			Map.entry("If-Modified-Since", "Tue, 01 Jan 1980 00:00:01 GMT"),
			Map.entry("If-None-Match", "W/\"269b-49773873e8\""),
			Map.entry("Sec-Fetch-Dest", "image"),
			Map.entry("Sec-Fetch-Mode", "no-cors"),
			Map.entry("Sec-Fetch-Site", "same-origin")
	);

	private Map<CharSequence, String> headers_26 = Map.ofEntries(
			Map.entry("If-Modified-Since", "Tue, 01 Jan 1980 00:00:01 GMT"),
			Map.entry("If-None-Match", "W/\"149-49773873e8\""),
			Map.entry("Sec-Fetch-Dest", "image"),
			Map.entry("Sec-Fetch-Mode", "no-cors"),
			Map.entry("Sec-Fetch-Site", "same-origin")
	);

	private Map<CharSequence, String> headers_27 = Map.ofEntries(
			Map.entry("If-Modified-Since", "Tue, 01 Jan 1980 00:00:01 GMT"),
			Map.entry("If-None-Match", "W/\"1d3e-49773873e8\""),
			Map.entry("Sec-Fetch-Dest", "image"),
			Map.entry("Sec-Fetch-Mode", "no-cors"),
			Map.entry("Sec-Fetch-Site", "same-origin")
	);

	private Map<CharSequence, String> headers_40 = Map.ofEntries(
			Map.entry("Accept", "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01"),
			Map.entry("If-Modified-Since", "Tue, 01 Jan 1980 00:00:01 GMT"),
			Map.entry("If-None-Match", "W/\"dbd-49773873e8\""),
			Map.entry("Sec-Fetch-Dest", "empty"),
			Map.entry("Sec-Fetch-Mode", "cors"),
			Map.entry("Sec-Fetch-Site", "same-origin"),
			Map.entry("X-Requested-With", "XMLHttpRequest")
	);

	private Map<CharSequence, String> headers_43 = Map.ofEntries(
			Map.entry("Accept", "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01"),
			Map.entry("If-Modified-Since", "Tue, 01 Jan 1980 00:00:01 GMT"),
			Map.entry("If-None-Match", "W/\"88d-49773873e8\""),
			Map.entry("Sec-Fetch-Dest", "empty"),
			Map.entry("Sec-Fetch-Mode", "cors"),
			Map.entry("Sec-Fetch-Site", "same-origin"),
			Map.entry("X-Requested-With", "XMLHttpRequest")
	);

	private Map<CharSequence, String> headers_52 = Map.ofEntries(
			Map.entry("Accept", "*/*"),
			Map.entry("If-Modified-Since", "Tue, 01 Jan 1980 00:00:01 GMT"),
			Map.entry("If-None-Match", "W/\"dbd-49773873e8\""),
			Map.entry("Sec-Fetch-Dest", "script"),
			Map.entry("Sec-Fetch-Mode", "no-cors"),
			Map.entry("Sec-Fetch-Site", "same-origin")
	);

	private Map<CharSequence, String> headers_53 = Map.ofEntries(
			Map.entry("Accept", "*/*"),
			Map.entry("If-Modified-Since", "Tue, 01 Jan 1980 00:00:01 GMT"),
			Map.entry("If-None-Match", "W/\"88d-49773873e8\""),
			Map.entry("Sec-Fetch-Dest", "script"),
			Map.entry("Sec-Fetch-Mode", "no-cors"),
			Map.entry("Sec-Fetch-Site", "same-origin")
	);

	private Map<CharSequence, String> headers_119 = Map.ofEntries(
			Map.entry("If-Modified-Since", "Tue, 01 Jan 1980 00:00:01 GMT"),
			Map.entry("If-None-Match", "W/\"18dd6-49773873e8\""),
			Map.entry("Sec-Fetch-Dest", "image"),
			Map.entry("Sec-Fetch-Mode", "no-cors"),
			Map.entry("Sec-Fetch-Site", "same-origin")
	);

	private Map<CharSequence, String> headers_149 = Map.ofEntries(
			Map.entry("Accept", "*/*"),
			Map.entry("Content-Type", "application/json; charset=utf-8"),
			Map.entry("Origin", GATEWAY_URL),
			Map.entry("Sec-Fetch-Dest", "empty"),
			Map.entry("Sec-Fetch-Mode", "cors"),
			Map.entry("Sec-Fetch-Site", "same-origin"),
			Map.entry("X-Requested-With", "XMLHttpRequest")
	);


	private ScenarioBuilder scn = scenario("RecordedSimulation")
			.exec(
					http("request_0")
							.get("/")
							.headers(headers_0)
							.resources(
									http("request_1")
											.get("/vendor/font-awesome/fonts/fontawesome-webfont.woff2?v=4.7.0")
											.headers(headers_1),
									http("request_2")
											.get("/navbar.html")
											.headers(headers_2),
									http("request_3")
											.get("/img/fixed-background-2.jpg")
											.headers(headers_3),
									http("request_4")
											.get("/login.html")
											.headers(headers_2),
									http("request_5")
											.get("/footer.html")
											.headers(headers_2),
									http("request_6")
											.get("/products")
											.headers(headers_6),
									http("request_7")
											.get("/img/logo.png")
											.headers(headers_3),
									http("request_8")
											.get("/img/logo-small.png")
											.headers(headers_3),
									http("request_9")
											.get("/img/payment.png")
											.headers(headers_3),
									http("request_10")
											.get("/static/images/smartwatch_square.jpg")
											.headers(headers_3),
									http("request_11")
											.get("/static/images/bottle_square.jpg")
											.headers(headers_3),
									http("request_12")
											.get("/static/images/bicycle_square.jpg")
											.headers(headers_3),
									http("request_13")
											.get("/js/jwt-decode.min.js")
											.headers(headers_13),
									http("request_14")
											.get("/static/images/basketball_square.jpg")
											.headers(headers_3),
									http("request_15")
											.get("/js/jwt-decode.min.js")
											.headers(headers_15),
									http("request_16")
											.get("/static/images/redpants_square.jpg")
											.headers(headers_3),
									http("request_17")
											.get("/static/images/yogamat_square.jpg")
											.headers(headers_3),
									http("request_18")
											.get("/userinfo")
											.headers(headers_6),
									http("request_19")
											.get("/static/images/weights_square.jpg")
											.headers(headers_3)
							)
			)
			.pause(1)
			.exec(
					http("request_20")
							.get("/detail.html?id=0b3a1e5a-e520-4f1e-bdf4-5f721f728bfd")
							.headers(headers_20)
							.resources(
									http("request_21")
											.get("/navbar.html")
											.headers(headers_21),
									http("request_22")
											.get("/footer.html")
											.headers(headers_22),
									http("request_23")
											.get("/login.html")
											.headers(headers_23),
									http("request_24")
											.get("/products/0b3a1e5a-e520-4f1e-bdf4-5f721f728bfd")
											.headers(headers_6),
									http("request_25")
											.get("/img/logo.png")
											.headers(headers_25),
									http("request_26")
											.get("/img/logo-small.png")
											.headers(headers_26),
									http("request_27")
											.get("/img/payment.png")
											.headers(headers_27),
									http("request_28")
											.get("/userinfo")
											.headers(headers_6),
									http("request_29")
											.get("/static/images/bottle_square.jpg")
											.headers(headers_3),
									http("request_30")
											.get("/static/images/bottle_thumb3.jpg")
											.headers(headers_3)
							)
			)
			.pause(1)
			.exec(
					http("request_31")
							.get("/catalog.html")
							.headers(headers_20)
							.resources(
									http("request_32")
											.get("/footer.html")
											.headers(headers_22),
									http("request_33")
											.get("/navbar.html")
											.headers(headers_21),
									http("request_34")
											.get("/catalogliveness")
											.headers(headers_6),
									http("request_35")
											.get("/login.html")
											.headers(headers_23),
									http("request_36")
											.get("/products")
											.headers(headers_6),
									http("request_37")
											.get("/img/logo.png")
											.headers(headers_25),
									http("request_38")
											.get("/img/logo-small.png")
											.headers(headers_26),
									http("request_39")
											.get("/img/payment.png")
											.headers(headers_27),
									http("request_40")
											.get("/js/client.js")
											.headers(headers_40),
									http("request_41")
											.get("/js/client.js")
											.headers(headers_13),
									http("request_42")
											.get("/static/images/redpants_square.jpg")
											.headers(headers_3),
									http("request_43")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_44")
											.get("/static/images/smartwatch_square.jpg")
											.headers(headers_3),
									http("request_45")
											.get("/static/images/bottle_square.jpg")
											.headers(headers_3),
									http("request_46")
											.get("/static/images/shoes_square.jpg")
											.headers(headers_3),
									http("request_47")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_48")
											.get("/userinfo")
											.headers(headers_6),
									http("request_49")
											.get("/static/images/bicycle_square.jpg")
											.headers(headers_3),
									http("request_50")
											.get("/static/images/basketball_square.jpg")
											.headers(headers_3),
									http("request_51")
											.get("/detail.html?id=3807bbe3-0f35-4d6a-a581-1d61814dd63f")
											.headers(headers_20),
									http("request_52")
											.get("/js/client.js")
											.headers(headers_52),
									http("request_53")
											.get("/js/jwt-decode.min.js")
											.headers(headers_53),
									http("request_54")
											.get("/footer.html")
											.headers(headers_22),
									http("request_55")
											.get("/login.html")
											.headers(headers_23),
									http("request_56")
											.get("/navbar.html")
											.headers(headers_21),
									http("request_57")
											.get("/products/3807bbe3-0f35-4d6a-a581-1d61814dd63f")
											.headers(headers_6),
									http("request_58")
											.get("/img/logo.png")
											.headers(headers_25),
									http("request_59")
											.get("/img/payment.png")
											.headers(headers_27),
									http("request_60")
											.get("/js/client.js")
											.headers(headers_40),
									http("request_61")
											.get("/img/logo-small.png")
											.headers(headers_26),
									http("request_62")
											.get("/static/images/weights_square.jpg")
											.headers(headers_3),
									http("request_63")
											.get("/js/client.js")
											.headers(headers_13),
									http("request_64")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_65")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_66")
											.get("/userinfo")
											.headers(headers_6),
									http("request_67")
											.get("/static/images/smartwatch_thumb3.jpg")
											.headers(headers_3),
									http("request_68")
											.get("/static/images/smartwatch_square.jpg")
											.headers(headers_3)
							)
			)
			.pause(1)
			.exec(
					http("request_69")
							.get("/contact.html")
							.headers(headers_20)
							.resources(
									http("request_70")
											.get("/footer.html")
											.headers(headers_22),
									http("request_71")
											.get("/login.html")
											.headers(headers_23),
									http("request_72")
											.get("/navbar.html")
											.headers(headers_21),
									http("request_73")
											.get("/img/logo.png")
											.headers(headers_25),
									http("request_74")
											.get("/img/payment.png")
											.headers(headers_27),
									http("request_75")
											.get("/js/client.js")
											.headers(headers_40),
									http("request_76")
											.get("/img/logo-small.png")
											.headers(headers_26),
									http("request_77")
											.get("/js/client.js")
											.headers(headers_13),
									http("request_78")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_79")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_80")
											.get("/userinfo")
											.headers(headers_6),
									http("request_81")
											.get("/footer.html")
											.headers(headers_22),
									http("request_82")
											.get("/login.html")
											.headers(headers_23),
									http("request_83")
											.get("/navbar.html")
											.headers(headers_21),
									http("request_84")
											.get("/catalogliveness")
											.headers(headers_6),
									http("request_85")
											.get("/products")
											.headers(headers_6),
									http("request_86")
											.get("/img/payment.png")
											.headers(headers_27),
									http("request_87")
											.get("/js/client.js")
											.headers(headers_40),
									http("request_88")
											.get("/img/logo.png")
											.headers(headers_25),
									http("request_89")
											.get("/img/logo-small.png")
											.headers(headers_26),
									http("request_90")
											.get("/js/client.js")
											.headers(headers_13),
									http("request_91")
											.get("/static/images/bottle_square.jpg")
											.headers(headers_3),
									http("request_92")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_93")
											.get("/static/images/redpants_square.jpg")
											.headers(headers_3),
									http("request_94")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_95")
											.get("/static/images/smartwatch_square.jpg")
											.headers(headers_3),
									http("request_96")
											.get("/static/images/basketball_square.jpg")
											.headers(headers_3),
									http("request_97")
											.get("/static/images/yogamat_square.jpg")
											.headers(headers_3),
									http("request_98")
											.get("/userinfo")
											.headers(headers_6),
									http("request_99")
											.get("/static/images/weights_square.jpg")
											.headers(headers_3),
									http("request_100")
											.get("/static/images/bicycle_square.jpg")
											.headers(headers_3),
									http("request_101")
											.get("/detail.html?id=408fa3e1-0455-4f6e-82f5-54e0bf97e607")
											.headers(headers_20),
									http("request_102")
											.get("/js/client.js")
											.headers(headers_52),
									http("request_103")
											.get("/js/jwt-decode.min.js")
											.headers(headers_53),
									http("request_104")
											.get("/footer.html")
											.headers(headers_22),
									http("request_105")
											.get("/login.html")
											.headers(headers_23),
									http("request_106")
											.get("/navbar.html")
											.headers(headers_21),
									http("request_107")
											.get("/products/408fa3e1-0455-4f6e-82f5-54e0bf97e607")
											.headers(headers_6),
									http("request_108")
											.get("/js/client.js")
											.headers(headers_40),
									http("request_109")
											.get("/img/payment.png")
											.headers(headers_27),
									http("request_110")
											.get("/img/logo-small.png")
											.headers(headers_26),
									http("request_111")
											.get("/js/client.js")
											.headers(headers_13),
									http("request_112")
											.get("/img/logo.png")
											.headers(headers_25),
									http("request_113")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_114")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_115")
											.get("/userinfo")
											.headers(headers_6),
									http("request_116")
											.get("/static/images/weights_thumb2.jpg")
											.headers(headers_3),
									http("request_117")
											.get("/static/images/weights_square.jpg")
											.headers(headers_3)
							)
			)
			.pause(2)
			.exec(
					http("request_118")
							.get("/index.html")
							.headers(headers_20)
							.resources(
									http("request_119")
											.get("/img/fixed-background-2.jpg")
											.headers(headers_119),
									http("request_120")
											.get("/login.html")
											.headers(headers_23),
									http("request_121")
											.get("/footer.html")
											.headers(headers_22),
									http("request_122")
											.get("/navbar.html")
											.headers(headers_21),
									http("request_123")
											.get("/products")
											.headers(headers_6),
									http("request_124")
											.get("/img/logo-small.png")
											.headers(headers_26),
									http("request_125")
											.get("/img/logo.png")
											.headers(headers_25),
									http("request_126")
											.get("/img/payment.png")
											.headers(headers_27),
									http("request_127")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_128")
											.get("/static/images/redpants_square.jpg")
											.headers(headers_3),
									http("request_129")
											.get("/static/images/bottle_square.jpg")
											.headers(headers_3),
									http("request_130")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_131")
											.get("/static/images/smartwatch_square.jpg")
											.headers(headers_3),
									http("request_132")
											.get("/userinfo")
											.headers(headers_6),
									http("request_133")
											.get("/oauth2/authorization/sso")
											.headers(headers_6),
									http("request_134")
											.get("/static/images/bicycle_square.jpg")
											.headers(headers_3),
									http("request_135")
											.get("/detail.html?id=533445d-530e-4a76-9398-5d16713b827b")
											.headers(headers_20),
									http("request_136")
											.get("/static/images/basketball_square.jpg")
											.headers(headers_3),
									http("request_137")
											.get("/js/jwt-decode.min.js")
											.headers(headers_53),
									http("request_138")
											.get("/login.html")
											.headers(headers_23),
									http("request_139")
											.get("/navbar.html")
											.headers(headers_21),
									http("request_140")
											.get("/footer.html")
											.headers(headers_22),
									http("request_141")
											.get("/products/533445d-530e-4a76-9398-5d16713b827b")
											.headers(headers_6),
									http("request_142")
											.get("/img/payment.png")
											.headers(headers_27),
									http("request_143")
											.get("/img/logo.png")
											.headers(headers_25),
									http("request_144")
											.get("/img/logo-small.png")
											.headers(headers_26),
									http("request_145")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_146")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_147")
											.get("/static/images/weights_square.jpg")
											.headers(headers_3),
									http("request_148")
											.get("/userinfo")
											.headers(headers_6),
									http("request_149")
											.post("/cart/item/add/guest")
											.headers(headers_149)
											.body(RawFileBody("0149_request.bin")),
									http("request_150")
											.get("/oauth2/authorization/sso")
											.headers(headers_6),
									http("request_151")
											.get("/img/payment.png")
											.headers(headers_27),
									http("request_152")
											.get("/img/logo.png")
											.headers(headers_25),
									http("request_153")
											.get("/navbar.html")
											.headers(headers_21),
									http("request_154")
											.get("/login.html")
											.headers(headers_23),
									http("request_155")
											.get("/footer.html")
											.headers(headers_22),
									http("request_156")
											.get("/img/logo-small.png")
											.headers(headers_26),
									http("request_157")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_158")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_159")
											.get("/userinfo")
											.headers(headers_6),
									http("request_160")
											.get("/img/fixed-background-2.jpg")
											.headers(headers_119),
									http("request_161")
											.get("/login.html")
											.headers(headers_23),
									http("request_162")
											.get("/navbar.html")
											.headers(headers_21),
									http("request_163")
											.get("/footer.html")
											.headers(headers_22),
									http("request_164")
											.get("/products")
											.headers(headers_6),
									http("request_165")
											.get("/img/payment.png")
											.headers(headers_27),
									http("request_166")
											.get("/img/logo-small.png")
											.headers(headers_26),
									http("request_167")
											.get("/img/logo.png")
											.headers(headers_25),
									http("request_168")
											.get("/static/images/yogamat_square.jpg")
											.headers(headers_3),
									http("request_169")
											.get("/static/images/smartwatch_square.jpg")
											.headers(headers_3),
									http("request_170")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_171")
											.get("/js/jwt-decode.min.js")
											.headers(headers_43),
									http("request_172")
											.get("/static/images/basketball_square.jpg")
											.headers(headers_3),
									http("request_173")
											.get("/static/images/redpants_square.jpg")
											.headers(headers_3),
									http("request_174")
											.get("/userinfo")
											.headers(headers_6),
									http("request_175")
											.get("/static/images/bicycle_square.jpg")
											.headers(headers_3)
							)
			);

	{
		setUp(scn.injectOpen(atOnceUsers(1000))).protocols(httpProtocol);
	}
}
