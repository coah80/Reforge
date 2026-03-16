package micheal65536.vienna.apiserver.routes.player;

import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import micheal65536.vienna.apiserver.routing.Request;
import micheal65536.vienna.apiserver.routing.Response;
import micheal65536.vienna.apiserver.routing.Router;
import micheal65536.vienna.apiserver.types.common.Rarity;
import micheal65536.vienna.apiserver.types.common.Rewards;
import micheal65536.vienna.apiserver.utils.EarthApiResponse;
import micheal65536.vienna.apiserver.utils.MapBuilder;
import micheal65536.vienna.apiserver.utils.TimeFormatter;
import micheal65536.vienna.db.EarthDB;

import java.util.HashMap;
import java.util.UUID;

public class ChallengesRouter extends Router
{
	public ChallengesRouter(@NotNull EarthDB earthDB)
	{
		this.addHandler(new Route.Builder(Request.Method.GET, "/player/challenges").build(), request ->
		{
			HashMap<String, Challenge> challenges = buildDefaultChallenges();

			return Response.okFromJson(new EarthApiResponse<>(new MapBuilder<>()
					.put("challenges", challenges)
					.put("activeSeasonChallenge", "00000000-0000-0000-0000-000000000001")
					.getMap()
			), EarthApiResponse.class);
		});

		this.addHandler(new Route.Builder(Request.Method.PUT, "/challenges/season/active/$challengeId").build(), request ->
		{
			String challengeId = request.getParameter("challengeId");
			return Response.okFromJson(new EarthApiResponse<>(new MapBuilder<>()
					.put("challenges", buildDefaultChallenges())
					.put("activeSeasonChallenge", challengeId)
					.getMap()
			), EarthApiResponse.class);
		});

		this.addHandler(new Route.Builder(Request.Method.POST, "/challenges/$challengeId/modifyState").build(), request ->
		{
			return Response.okFromJson(new EarthApiResponse<>(new Object()), EarthApiResponse.class);
		});

		this.addHandler(new Route.Builder(Request.Method.POST, "/challenges/reset").build(), request ->
		{
			return Response.okFromJson(new EarthApiResponse<>(new MapBuilder<>()
					.put("challenges", buildDefaultChallenges())
					.put("activeSeasonChallenge", "00000000-0000-0000-0000-000000000001")
					.getMap()
			), EarthApiResponse.class);
		});

		this.addHandler(new Route.Builder(Request.Method.POST, "/challenges/timed/generate").build(), request ->
		{
			long endOfDay = getEndOfDayMillis();

			HashMap<String, Challenge> timedChallenges = new HashMap<>();

			timedChallenges.put(UUID.randomUUID().toString(), new Challenge(
					UUID.randomUUID().toString(), null, UUID.randomUUID().toString(),
					"PersonalTimed", "Regular", "collection", null, 0,
					TimeFormatter.formatTime(endOfDay), "Active", false, 0, 0, 5,
					new String[0], "And",
					new Rewards(10, null, null, new Rewards.Item[0], new String[0], new Rewards.Challenge[0], new String[0], new Rewards.UtilityBlock[0]),
					new Object()
			));

			timedChallenges.put(UUID.randomUUID().toString(), new Challenge(
					UUID.randomUUID().toString(), null, UUID.randomUUID().toString(),
					"PersonalTimed", "Regular", "building", null, 1,
					TimeFormatter.formatTime(endOfDay), "Active", false, 0, 0, 3,
					new String[0], "And",
					new Rewards(null, 5, null, new Rewards.Item[0], new String[0], new Rewards.Challenge[0], new String[0], new Rewards.UtilityBlock[0]),
					new Object()
			));

			timedChallenges.put(UUID.randomUUID().toString(), new Challenge(
					UUID.randomUUID().toString(), null, UUID.randomUUID().toString(),
					"PersonalTimed", "Regular", "collection", null, 2,
					TimeFormatter.formatTime(endOfDay), "Active", false, 0, 0, 10,
					new String[0], "And",
					new Rewards(25, null, null, new Rewards.Item[0], new String[0], new Rewards.Challenge[0], new String[0], new Rewards.UtilityBlock[0]),
					new Object()
			));

			return Response.okFromJson(new EarthApiResponse<>(new MapBuilder<>()
					.put("challenges", timedChallenges)
					.put("activeSeasonChallenge", "00000000-0000-0000-0000-000000000001")
					.getMap()
			), EarthApiResponse.class);
		});

		this.addHandler(new Route.Builder(Request.Method.DELETE, "/challenges/continuous/$challengeId/remove").build(), request ->
		{
			return Response.okFromJson(new EarthApiResponse<>(new Object()), EarthApiResponse.class);
		});
	}

	@NotNull
	private static HashMap<String, Challenge> buildDefaultChallenges()
	{
		long seasonEnd = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000;

		HashMap<String, Challenge> challenges = new HashMap<>();

		// season challenges required for journal to load
		challenges.put("00000000-0000-0000-0000-000000000001", new Challenge(
				"00000000-0000-0000-0000-000000000001", null,
				"00000000-0000-0000-0000-000000000001",
				"Season", "Regular", "season_1", null, 0,
				TimeFormatter.formatTime(seasonEnd), "Active", false, 0, 0, 10,
				new String[0], "And",
				new Rewards(null, null, null, new Rewards.Item[0], new String[0], new Rewards.Challenge[0],
						new String[]{"230f5996-04b2-4f0e-83e5-4056c7f1d946"}, new Rewards.UtilityBlock[0]),
				new Object()
		));

		challenges.put("00000000-0000-0000-0000-000000000002", new Challenge(
				"00000000-0000-0000-0000-000000000002", null,
				"00000000-0000-0000-0000-000000000001",
				"Season", "Regular", "season_1", null, 1,
				TimeFormatter.formatTime(seasonEnd), "Locked", false, 0, 0, 1,
				new String[0], "And",
				new Rewards(null, null, null, new Rewards.Item[0], new String[0], new Rewards.Challenge[0],
						new String[]{"d7725840-4376-44fc-9220-585f45775371"}, new Rewards.UtilityBlock[0]),
				new Object()
		));

		challenges.put("00000000-0000-0000-0000-000000000010", new Challenge(
				"00000000-0000-0000-0000-000000000010", null,
				"00000000-0000-0000-0000-000000000010",
				"Career", "Regular", "collection", null, 0,
				TimeFormatter.formatTime(seasonEnd), "Active", false, 0, 0, 5,
				new String[0], "And",
				new Rewards(25, null, null, new Rewards.Item[0], new String[0], new Rewards.Challenge[0], new String[0], new Rewards.UtilityBlock[0]),
				new Object()
		));

		challenges.put("00000000-0000-0000-0000-000000000011", new Challenge(
				"00000000-0000-0000-0000-000000000011", null,
				"00000000-0000-0000-0000-000000000011",
				"Career", "Regular", "building", null, 1,
				TimeFormatter.formatTime(seasonEnd), "Active", false, 0, 0, 3,
				new String[0], "And",
				new Rewards(50, null, null, new Rewards.Item[0], new String[0], new Rewards.Challenge[0], new String[0], new Rewards.UtilityBlock[0]),
				new Object()
		));

		challenges.put("00000000-0000-0000-0000-000000000012", new Challenge(
				"00000000-0000-0000-0000-000000000012", null,
				"00000000-0000-0000-0000-000000000012",
				"Career", "Regular", "building", null, 2,
				TimeFormatter.formatTime(seasonEnd), "Active", false, 0, 0, 1,
				new String[0], "And",
				new Rewards(100, null, null, new Rewards.Item[0], new String[0], new Rewards.Challenge[0], new String[0], new Rewards.UtilityBlock[0]),
				new Object()
		));

		return challenges;
	}

	private static long getEndOfDayMillis()
	{
		long now = System.currentTimeMillis();
		long msInDay = 24 * 60 * 60 * 1000L;
		return (now / msInDay + 1) * msInDay;
	}

	private record Challenge(
			@NotNull String referenceId,
			@Nullable String parentId,
			@NotNull String groupId,
			@NotNull String duration,
			@NotNull String type,
			@NotNull String category,
			@Nullable Rarity rarity,
			int order,
			@NotNull String endTimeUtc,
			@NotNull String state,
			boolean isComplete,
			int percentComplete,
			int currentCount,
			int totalThreshold,
			@NotNull String[] prerequisiteIds,
			@NotNull String prerequisiteLogicalCondition,
			@NotNull Rewards rewards,
			@NotNull Object clientProperties
	)
	{
	}
}
