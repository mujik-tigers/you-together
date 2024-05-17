package site.youtogether.util;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomUtil {

	private static final AtomicLong userId = new AtomicLong();
	private static final AtomicLong chatId = new AtomicLong();
	private static final AtomicLong videoNumber = new AtomicLong();

	public static Long generateUserId() {
		return userId.incrementAndGet();
	}

	public static Long generateChatId() {
		return chatId.incrementAndGet();
	}

	public static Long generateVideoNumber() {
		return videoNumber.incrementAndGet();
	}

	/**
	 * generate random code
	 * using a-z A-Z 0-9
	 */
	public static String generateRandomCode(int length) {
		String randomString = UUID.randomUUID().toString().replaceAll("-", "");

		return randomString.substring(0, length);
	}

	/**
	 * generate random user nickname
	 * sample list size is 20
	 */
	public static String generateUserNickname() {
		List<String> samples = List.of(
			"MysticTiger", "SilverPhoenix", "ElectricWanderer", "CrimsonDragon", "EmeraldSpecter",
			"MidnightRider", "VelvetWhisperer", "CosmicStrider", "SolarGoddess", "ArcticShadow",
			"EnigmaticSphinx", "ScarletSorcerer", "CelestialWatcher", "LunarJester", "SapphireDreamer",
			"GoldenGlider", "CrimsonFalcon", "EchoingWhisper", "EmberPhoenix", "RadiantRebel"
		);

		int prefixIndex = ThreadLocalRandom.current().nextInt(samples.size());
		int suffixNumber = ThreadLocalRandom.current().nextInt(1000);

		return samples.get(prefixIndex) + suffixNumber;
	}

}
