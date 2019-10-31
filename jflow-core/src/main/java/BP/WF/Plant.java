package BP.WF;

/** 
 运行平台
 
*/
public enum Plant
{
	CCFlow,
	JFlow;

	public int getValue()
	{
		return this.ordinal();
	}

	public static Plant forValue(int value)
	{
		return values()[value];
	}
}