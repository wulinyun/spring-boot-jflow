package BP.Sys;

import BP.DA.*;
import BP.En.*;

public enum AthCtrlWay
{
	/** 
	 表单主键
	 
	*/
	PK,
	/** 
	 FID
	 
	*/
	FID,
	/** 
	 父流程ID
	 
	*/
	PWorkID,
	/** 
	 仅仅查看自己的
	 
	*/
	MySelfOnly,
	 /** 
	 工作ID,对流程有效.
	 
	*/
	WorkID;

	public int getValue()
	{
		return this.ordinal();
	}

	public static AthCtrlWay forValue(int value)
	{
		return values()[value];
	}
}