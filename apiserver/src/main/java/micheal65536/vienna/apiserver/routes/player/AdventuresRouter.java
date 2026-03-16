package micheal65536.vienna.apiserver.routes.player;

import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import micheal65536.vienna.apiserver.routing.Request;
import micheal65536.vienna.apiserver.routing.Response;
import micheal65536.vienna.apiserver.routing.Router;
import micheal65536.vienna.apiserver.routing.ServerErrorException;
import micheal65536.vienna.apiserver.utils.EarthApiResponse;
import micheal65536.vienna.apiserver.utils.MapBuilder;
import micheal65536.vienna.apiserver.utils.TimeFormatter;
import micheal65536.vienna.db.DatabaseException;
import micheal65536.vienna.db.EarthDB;
import micheal65536.vienna.db.model.player.Inventory;

import java.util.HashMap;
import java.util.UUID;

public class AdventuresRouter extends Router
{
	public AdventuresRouter(@NotNull EarthDB earthDB)
	{
		this.addHandler(new Route.Builder(Request.Method.GET, "/adventures/scrolls").build(), request ->
		{
			try
			{
				String playerId = request.getContextData("playerId");
				Inventory inventory = (Inventory) new EarthDB.Query(false)
						.get("inventory", playerId, Inventory.class)
						.execute(earthDB)
						.get("inventory").value();

				return Response.okFromJson(new EarthApiResponse<>(new MapBuilder<>()
						.put("scrolls", new HashMap<>())
						.put("isNew", false)
						.getMap()
				), EarthApiResponse.class);
			}
			catch (DatabaseException exception)
			{
				throw new ServerErrorException(exception);
			}
		});

		this.addHandler(new Route.Builder(Request.Method.POST, "/adventures/scrolls/$itemId")
				.addQueryParameter("lat", "lat")
				.addQueryParameter("lon", "lon")
				.build(), request ->
		{
			String itemId = request.getParameter("itemId");
			String playerId = request.getContextData("playerId");

			float lat;
			float lon;
			try
			{
				lat = Float.parseFloat(request.getParameter("lat", "0"));
				lon = Float.parseFloat(request.getParameter("lon", "0"));
			}
			catch (NumberFormatException e)
			{
				return Response.badRequest();
			}

			String encounterId = UUID.randomUUID().toString();
			long now = System.currentTimeMillis();
			long expirationMs = 10 * 60 * 1000L; // 10 minutes

			return Response.okFromJson(new EarthApiResponse<>(new MapBuilder<>()
					.put("result", new MapBuilder<>()
							.put("id", encounterId)
							.put("coordinate", new MapBuilder<>()
									.put("latitude", lat)
									.put("longitude", lon)
									.getMap()
							)
							.put("spawnTime", TimeFormatter.formatTime(now))
							.put("expirationTime", TimeFormatter.formatTime(now + expirationMs))
							.put("encounterType", "Short")
							.put("icon", "genoa:adventure_scroll_encounter")
							.put("state", "Pristine")
							.getMap()
					)
					.getMap()
			), EarthApiResponse.class);
		});
	}
}
