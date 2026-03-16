package micheal65536.vienna.apiserver.routes.player;

import org.jetbrains.annotations.NotNull;

import micheal65536.vienna.apiserver.routing.Request;
import micheal65536.vienna.apiserver.routing.Response;
import micheal65536.vienna.apiserver.routing.Router;
import micheal65536.vienna.apiserver.routing.ServerErrorException;
import micheal65536.vienna.apiserver.utils.EarthApiResponse;
import micheal65536.vienna.apiserver.utils.MapBuilder;
import micheal65536.vienna.db.DatabaseException;
import micheal65536.vienna.db.EarthDB;
import micheal65536.vienna.db.model.player.Inventory;
import micheal65536.vienna.db.model.player.Profile;

import java.util.HashMap;

public class StoreRouter extends Router
{
	public StoreRouter(@NotNull EarthDB earthDB)
	{
		this.addHandler(new Route.Builder(Request.Method.POST, "/commerce/purchase").build(), request ->
		{
			try
			{
				String playerId = request.getContextData("playerId");
				record PurchaseRequest(@NotNull String itemId, int expectedPurchasePrice)
				{
				}
				PurchaseRequest body = request.getBodyAsJson(PurchaseRequest.class);
				if (body == null || body.itemId == null)
				{
					return Response.badRequest();
				}

				EarthDB.Results results = new EarthDB.Query(true)
						.get("profile", playerId, Profile.class)
						.get("inventory", playerId, Inventory.class)
						.then(results1 ->
						{
							EarthDB.Query query = new EarthDB.Query(true);

							Profile profile = (Profile) results1.get("profile").value();
							Inventory inventory = (Inventory) results1.get("inventory").value();

							int totalRubies = profile.rubies.purchased + profile.rubies.earned;
							if (totalRubies < body.expectedPurchasePrice)
							{
								return query;
							}

							if (!profile.rubies.spend(body.expectedPurchasePrice))
							{
								return query;
							}

							inventory.addItems(body.itemId, 1);

							query.update("profile", playerId, profile).update("inventory", playerId, inventory);
							return query;
						})
						.execute(earthDB);

				return Response.okFromJson(new EarthApiResponse<>(new MapBuilder<>()
						.put("success", true)
						.getMap(), new EarthApiResponse.Updates(results)
				), EarthApiResponse.class);
			}
			catch (DatabaseException exception)
			{
				throw new ServerErrorException(exception);
			}
		});

		this.addHandler(new Route.Builder(Request.Method.POST, "/commerce/purchaseV2").build(), request ->
		{
			return Response.okFromJson(new EarthApiResponse<>(new MapBuilder<>()
					.put("success", true)
					.getMap()
			), EarthApiResponse.class);
		});

		this.addHandler(new Route.Builder(Request.Method.GET, "/commerce/storeItemInfo").build(), request ->
		{
			return Response.okFromJson(new EarthApiResponse<>(new MapBuilder<>()
					.put("items", new HashMap<>())
					.getMap()
			), EarthApiResponse.class);
		});

		this.addHandler(new Route.Builder(Request.Method.POST, "/commerce/redeemReceipt").build(), request ->
		{
			return Response.okFromJson(new EarthApiResponse<>(new MapBuilder<>()
					.put("success", true)
					.getMap()
			), EarthApiResponse.class);
		});

		this.addHandler(new Route.Builder(Request.Method.POST, "/commerce/redeemReceiptV2").build(), request ->
		{
			return Response.okFromJson(new EarthApiResponse<>(new MapBuilder<>()
					.put("success", true)
					.getMap()
			), EarthApiResponse.class);
		});
	}
}
