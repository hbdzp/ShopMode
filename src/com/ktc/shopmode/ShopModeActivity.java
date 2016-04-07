package com.ktc.shopmode;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ShopModeActivity extends Activity {
	boolean isAutoScroll = false;
	protected static final int SCROLL_TO_NEXT = 1;
	protected static final int SHOW_LISTVIEW = 2;
	ListView mListView;
	SurfaceView sf;
	// 负责配合mediaPlayer显示视频图像播放的surfaceView
	MyAdapter adapter;
	int offset;
	int currentPosition = 0;
	int[] mItems = { R.drawable.selector_iv_perfect, //
			R.drawable.selector_iv_incredible, //
			R.drawable.selector_iv_clearsound, //
			R.drawable.selector_iv_dolby, //
			R.drawable.selector_iv_avl, //
			R.drawable.selector_iv_dtv, //
			R.drawable.selector_iv_usb, //
			R.drawable.selector_iv_hdmi, //
			R.drawable.selector_iv_wifi, //
			R.drawable.selector_iv_smarttv, //
			R.drawable.selector_iv_unb,//
	};
	int[] mItemsDesc = { R.string.PerfectPixel, //
			R.string.IncredibleSurround, //
			R.string.ClearSound, //
			R.string.DolbyDigitalPlus, //
			R.string.AVL, //
			R.string.DTV, //
			R.string.USB, //
			R.string.HDMI, //
			R.string.WiFi, //
			R.string.SmartTV, //
			R.string.UNB, //
	};
	private static Handler mHandler;
	private Timer mTimer = new Timer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopmode);
		initData();
		initView();
		starPlayVideo();
	}

	private void initData() {
		// TODO Auto-generated method stub
		mListView = (ListView) findViewById(R.id.lv_shopmode);
		sf = (SurfaceView) findViewById(R.id.surfaceview_shopmode);
		adapter = new MyAdapter();
		mListView.setAdapter(adapter);
		offset = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics())
				+ 0.5f);
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SHOW_LISTVIEW:
					mListView.smoothScrollByOffset(offset);
					adapter.notifyDataSetChanged();
					TranslateAnimation localTranslateAnimation = new TranslateAnimation(
							TranslateAnimation.RELATIVE_TO_SELF, //
							-1.0F, //
							TranslateAnimation.RELATIVE_TO_SELF, //
							0.0F, //
							TranslateAnimation.RELATIVE_TO_SELF, //
							0.0F, //
							TranslateAnimation.RELATIVE_TO_SELF, //
							0.0F //
					);
					AlphaAnimation alphaAnimation = new AlphaAnimation(0.5F, 1.0F); // 透明度动画
					AnimationSet animationSet = new AnimationSet(true);
					animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
					animationSet.addAnimation(localTranslateAnimation);
					animationSet.addAnimation(alphaAnimation);
					animationSet.setDuration(500);
					mListView.startAnimation(animationSet);
					mListView.setVisibility(View.VISIBLE);
					startAutoScroll();
					break;
				case SCROLL_TO_NEXT:
					currentPosition++;
					currentPosition %= mItems.length;
					mListView.smoothScrollBy(offset, 1500);
					adapter.notifyDataSetChanged();
					break;
				default:
					break;
				}
			}

		};
	}

	private void initView() {
		// TODO Auto-generated method stub
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setVisibility(View.GONE);
		mListView.setFocusable(false);
		mListView.setCacheColorHint(Color.TRANSPARENT);

	}

	private void starPlayVideo() {
		final SurfaceHolder surfaceHolder = sf.getHolder();
		surfaceHolder.addCallback(new Callback() {
			private MediaPlayer player;

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				player = new MediaPlayer();
				player.setAudioStreamType(AudioManager.STREAM_MUSIC);
				player.setDisplay(surfaceHolder);
				// 设置显示视频显示在SurfaceView上
				try {
					String path = Environment.getExternalStorageDirectory().getAbsolutePath()
							+ "/4K_demo_2014_140430_HD_version_SURR_HD_USB.mp4";
					Log.i("SHOPMENU", path);
					player.setDataSource(path);
					player.prepareAsync();
					player.start();
					Timer t = new Timer();
					t.schedule(new TimerTask() {

						@Override
						public void run() {
							Message message = mHandler.obtainMessage();
							message.what = SHOW_LISTVIEW;
							mHandler.sendMessage(message);
						}
					}, 3000);
					player.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							player.start();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				if (player.isPlaying()) {
					player.stop();
				}
				player.release();
			}
		});

	}
	/**
	 * listview开始自动滚动
	 */
	private void startAutoScroll() {
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				Message message = mHandler.obtainMessage();
				message.what = SCROLL_TO_NEXT;
				mHandler.sendMessage(message);
			}
		}, 0, 8000);
		isAutoScroll = true;
	}

	private void stopAutoScroll() {
		mTimer.cancel();
		isAutoScroll = false;
		// 隐藏listview
		TranslateAnimation translateAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, //
				0.0F, //
				TranslateAnimation.RELATIVE_TO_SELF, //
				-1.0F, //
				TranslateAnimation.RELATIVE_TO_SELF, //
				0.0F, //
				TranslateAnimation.RELATIVE_TO_SELF, //
				0.0F//
		);
		AlphaAnimation alphaAnimation = new AlphaAnimation(1.0F, 0.5F); // 透明度动画
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.setInterpolator(new AccelerateInterpolator());
		animationSet.addAnimation(translateAnimation);
		animationSet.addAnimation(alphaAnimation);
		animationSet.setDuration(500);
		mListView.startAnimation(animationSet);
		mListView.setVisibility(View.GONE);
	}

	private class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mItems.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = null;
			ViewHolder holder;
			if (convertView != null) {
				v = convertView;
				holder = (ViewHolder) convertView.getTag();
			} else {
				v = View.inflate(getApplicationContext(), R.layout.item, null);
				holder = new ViewHolder();
				holder.itemImage = (ImageView) v.findViewById(R.id.iv_item);
				holder.itemTextView = (TextView) v.findViewById(R.id.tv_item_desc);
				v.setTag(holder);
			}
			int count = (position + currentPosition) % mItems.length;
			holder.itemImage.setImageResource(mItems[count]);
			holder.itemTextView.setVisibility(View.GONE);
			holder.itemTextView.setText(mItemsDesc[count]);
			holder.itemImage.setFocusable(false);

			if (position == 2) {
				holder.itemTextView.setVisibility(View.VISIBLE);
				holder.itemImage.setFocusable(true);
				// 添加当前条目进场效果
				TranslateAnimation localTranslateAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, //
						-1.0F, //
						TranslateAnimation.RELATIVE_TO_SELF, //
						0.0F, //
						TranslateAnimation.RELATIVE_TO_SELF, //
						0.0F, //
						TranslateAnimation.RELATIVE_TO_SELF, //
						0.0F //
				);
				AlphaAnimation alphaAnimation = new AlphaAnimation(0.5F, 1.0F); // 透明度动画
				AnimationSet animationSet = new AnimationSet(true);
				animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
				animationSet.addAnimation(localTranslateAnimation);
				animationSet.addAnimation(alphaAnimation);
				animationSet.setDuration(1500);
				holder.itemTextView.startAnimation(animationSet);
			}
			return v;
		}
	}

	public static class ViewHolder {
		ImageView itemImage;
		TextView itemTextView;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (isAutoScroll) {
				stopAutoScroll();
			} else {
				finish();
			}
			break;
		case KeyEvent.KEYCODE_POWER:
			return super.onKeyDown(keyCode, event);
		default:
		}
		return true;
	}
}
