/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.location.Location;
import ch.usi.inf.bp.losha.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * Overlay used to display a radius from a point which can be expanded
 * scrolling a SeekBar placed over the map, and just a point in case we
 * are selecting a fake location.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class SelectLocationOverlay extends Overlay {
	private GeoPoint geoPoint;
	private final boolean selectRadius;
	private final MyMapActivity map;

	public SelectLocationOverlay(final MyMapActivity map, final boolean selectRadius) {
		super();
		this.selectRadius = selectRadius;
		this.map = map;
	}

	@Override
	public void draw(final Canvas canvas, final MapView mapView,
			final boolean shadow) {
		super.draw(canvas, mapView, shadow);
		final Projection projection = mapView.getProjection();

		if (geoPoint == null) {
			return;
		}

		final Point screenPoint = new Point();
		projection.toPixels(geoPoint, screenPoint);

		final Bitmap markerImage = BitmapFactory.decodeResource(
				map.getResources(), R.drawable.pin);

		if (selectRadius) {
			final int radius = map.getRadius();

			if (radius != 0) {
				drawMarker(canvas, mapView, map.getRadius()+"m");
			} else {
				drawMarker(canvas, mapView, null);
			}
		}

		canvas.drawBitmap(markerImage, screenPoint.x - markerImage.getWidth()
				/ 2, screenPoint.y - markerImage.getHeight(), null);
	}

	private void drawMarker(final Canvas canvas, final MapView mapView, final String text) {

		// Create the paint objects
		final Paint backPaint = new Paint();
		backPaint.setARGB(200, 200, 200, 200);
		backPaint.setAntiAlias(true);

		final Paint paint = new Paint();
		paint.setTextSize(20);
		paint.setARGB(255, 10, 10, 255);
		paint.setAntiAlias(true);
		paint.setFakeBoldText(true);

		Paint accuracyPaint;
		Point center;
		Point left;

		accuracyPaint = new Paint();
		accuracyPaint.setAntiAlias(true);
		accuracyPaint.setStrokeWidth(2.0f);

		center = new Point();
		left = new Point();
		double latitude;
		double longitude;
		float accuracy;
		final Projection projection = mapView.getProjection();

		latitude = geoPoint.getLatitudeE6() / 1E6;
		longitude = geoPoint.getLongitudeE6() / 1E6;
		accuracy = map.getRadius();

		final float[] result = new float[1];

		Location.distanceBetween(latitude, longitude, latitude, longitude + 1,
				result);
		final float longitudeLineDistance = result[0];

		final GeoPoint leftGeo = new GeoPoint((int) (latitude * 1E6),
				(int) ((longitude - accuracy / longitudeLineDistance) * 1E6));
		projection.toPixels(leftGeo, left);
		projection.toPixels(geoPoint, center);
		final int radius = center.x - left.x;

		// draw outer circle
		accuracyPaint.setColor(0xff6666ff);
		accuracyPaint.setStyle(Style.STROKE);
		canvas.drawCircle(center.x, center.y, radius, accuracyPaint);

		// draw circle fill
		accuracyPaint.setColor(0x186666ff);
		accuracyPaint.setStyle(Style.FILL);
		canvas.drawCircle(center.x, center.y, radius, accuracyPaint);

		if (text != null) {
			final float textWidth = paint.measureText(text);
			final float textHeight = paint.getTextSize();
			canvas.drawText(text, center.x - textWidth / 2, center.y + textHeight, paint);
		}
	}

	public GeoPoint getPoint() {
		return geoPoint;
	}

	@Override
	public boolean onTap(final GeoPoint point, final MapView mapView) {
		geoPoint = point;
		return false;
	}
}