package com.mapbox.flutter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;

import android.support.annotation.NonNull;

import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.NativeMapView;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.renderer.MapRenderer;
import com.mapbox.mapboxsdk.net.ConnectivityReceiver;
import com.mapbox.mapboxsdk.storage.FileSource;

public class FlutterMap implements NativeMapView.Callbacks {
  private Context context;
  private MapboxMapOptions mapboxMapOptions;
  private NativeMapView nativeMapView;
  private MapRenderer mapRenderer;
  private int width;
  private int height;

  public FlutterMap(Context context, MapboxMapOptions options,
                    SurfaceTexture surfaceTexture, int width, int height) {
    this.context = context;
    this.mapboxMapOptions = options;
    this.width = width;
    this.height = height;

    String localFontFamily = mapboxMapOptions.getLocalIdeographFontFamily();
    boolean translucentSurface = mapboxMapOptions.getTranslucentTextureSurface();
    mapRenderer = new SurfaceTextureMapRenderer(context, surfaceTexture, width, height, localFontFamily, translucentSurface);

    nativeMapView = new NativeMapView(context, this, mapRenderer);
    nativeMapView.setStyleUrl(mapboxMapOptions.getStyle());
    nativeMapView.resizeView(width, height);
    nativeMapView.setReachability(ConnectivityReceiver.instance(context).isConnected(context));
    nativeMapView.update();

    CameraPosition cameraPosition = mapboxMapOptions.getCamera();
    if (cameraPosition != null) {
        nativeMapView.jumpTo(cameraPosition.bearing, cameraPosition.target,
            cameraPosition.tilt, cameraPosition.zoom);
    }
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public void addOnMapChangedListener(@NonNull MapView.OnMapChangedListener listener) {

  }

  public void removeOnMapChangedListener(@NonNull MapView.OnMapChangedListener listener) {

  }

  public void onMapChange(int rawChange) {

  }

  public boolean post(Runnable runnable) {
    return true;
  }

  public Bitmap getViewContent() {
    return null;
  }

  public void onStart() {
    ConnectivityReceiver.instance(context).activate();
    FileSource.getInstance(context).activate();

    mapRenderer.onStart();
  }

  public void onResume() {
    mapRenderer.onResume();
  }

  public void onPause() {
    mapRenderer.onPause();
  }

  public void onStop() {
    mapRenderer.onStop();

    ConnectivityReceiver.instance(context).deactivate();
    FileSource.getInstance(context).deactivate();
  }

  public void onDestroy() {
    // null when destroying an activity programmatically mapbox-navigation-android/issues/503
    nativeMapView.destroy();
    mapRenderer.onDestroy();
  }

  public void moveBy(double dx, double dy, long duration) {
    nativeMapView.moveBy(dx, dy, duration);
  }

  public double getZoom() {
    return nativeMapView.getZoom();
  }

  public void zoom(double zoom, PointF focalPoint, long duration) {
    nativeMapView.setZoom(zoom, focalPoint, duration);
  }
}
