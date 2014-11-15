package com.IYYX.cardboard.myAPIs;

public class TcpManager {
	private static boolean bIsInitiated=false;
	public static void initiate(String serverIPorHostname){
		bIsInitiated=true;
	}
	public static void setListener(OnCallSucceedListener listener){}
	public static void setListener(OnBeingCalledListener listener){}
	public static void setListener(OnNewDataListener listener){}
	public static void disableOnNewDataListener(){}
	public static boolean isInitiated(){return bIsInitiated;}
	public static void call(String contactName){}
	
	public static void sendObj(Object obj){}
	public static Object getLatestObj(){return null;}
	
	public static interface OnCallSucceedListener{
		public void OnCallSucceed();
	}
	public static interface OnBeingCalledListener{
		public void OnBeingCalled(String contactName);
	}
	public static interface OnNewDataListener{
		public void OnNewData();
	}
}