package BP.WF.Template;


/** 
 显示控制方式
 
*/
public enum SFShowCtrl
{
	/** 
	 所有的子线程都可以看到
	 
	*/
	All,
	/** 
	 仅仅查看我自己的
	 
	*/
	MySelf;

	public int getValue()
	{
		return this.ordinal();
	}

	public static SFShowCtrl forValue(int value)
	{
		return values()[value];
	}
}