package com.IYYX.cardboard.myAPIs;

public class TcpManager {
	static void initiate(){}
	static void setListener(OnCallSucceedListener listener){}
	static void setListener(OnBeingCalledListener listener){}
	static void setListener(OnNewDataListener listener){}
	static void disableOnNewDataListener(){}
	static boolean isInitiated(){return false;}
	static void connect(String serverIPorHostname) {}
	static void call(String contactName){}
	
	static void sendObj(Object obj){}
	static void getLatestObj(Object obj){}
	
	public static interface OnCallSucceedListener{
		public void OnCallSucceed();
	}
	public static interface OnBeingCalledListener{
		public void OnBeingCalled();
	}
	public static interface OnNewDataListener{
		public void OnNewData();
	}
}