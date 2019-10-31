package BP.En;

/**
 * NoEntity 的摘要说明。
 */
public abstract class EntityMyPK extends Entity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// 基本属性
	@Override
	public String getPK()
	{
		return "MyPK";
	}
	
	/**
	 * 集合类名称
	 */
	public String getMyPK()
	{
		return this.GetValStringByKey(EntityMyPKAttr.MyPK);
	}
	
	public void setMyPK(String value)
	{
		this.SetValByKey(EntityMyPKAttr.MyPK, value);
	}
	
	// 构造
	public EntityMyPK()
	{
	}
	
	/**
	 * class Name
	 * 
	 * @param _MyPK
	 *            _MyPK
	 * @throws Exception 
	 */
	protected EntityMyPK(String _MyPK) throws Exception
	{
		this.setMyPK(_MyPK);
		this.Retrieve();
	}
	
	/**
	 * 初始化MyPK
	 * @return
	 * @throws Exception
	 */
	public String InitMyPKVals() throws Exception {
		return this.getMyPK();
	}
}