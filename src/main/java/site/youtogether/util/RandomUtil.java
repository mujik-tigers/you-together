package site.youtogether.util;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomUtil {

	/**
	 * generate random room code
	 * length is 10
	 * using a-z A-Z 0-9
	 */
	public static String generateRoomCode() {
		String randomString = UUID.randomUUID().toString().replaceAll("-", "");

		return randomString.substring(0, 10);
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

		int randomIndex = ThreadLocalRandom.current().nextInt(samples.size());

		return samples.get(randomIndex);
	}

}
