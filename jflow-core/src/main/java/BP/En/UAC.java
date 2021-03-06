package BP.En;

import BP.DA.DBAccess;
import BP.DA.DataTable;
import BP.DA.Paras;
import BP.Sys.SystemConfig;
import BP.Web.WebUser;

/**
 * 访问控制
 */
public class UAC
{
	 /// <summary>
    /// 从权限管理系统里装载数据.
    /// </summary>
    private static final String DataRow = null;
	public void LoadRightFromCCGPM(Entity en) throws Exception
    {
        String sql = "SELECT Tag1  FROM V_GPM_EmpMenu WHERE  FK_Emp='"+WebUser.getNo()+"'  AND Url LIKE '%" + en.toString()+ "%'  ";
        DataTable dt = DBAccess.RunSQLReturnTable(sql);
        for (BP.DA.DataRow dr : dt.Rows)
        {
            String tag = dr.getValue(0).toString();

            if (tag.contains("Insert") == true)
                this.IsInsert = true;          

            if (tag.contains("Update") == true)
                this.IsUpdate = true;
          
            if (tag.contains("Delete") == true)
                this.IsDelete = true;
         
        }

    }
    
	// 常用方法
	public final void Readonly()
	{
		this.IsUpdate = false;
		this.IsDelete = false;
		this.IsInsert = false;
		this.IsAdjunct = false;
		this.IsView = true;
	}
	
	/**
	 * 全部开放
	 */
	public final void OpenAll()
	{
		this.IsUpdate = true;
		this.IsDelete = true;
		this.IsInsert = true;
		this.IsAdjunct = false;
		this.IsView = true;
	}
	
	/**
	 * 为一个岗位设置全部的权限
	 * 
	 * @param fk_station
	 * @throws Exception 
	 */
	public final void OpenAllForStation(String fk_station) throws Exception
	{
		Paras ps = new Paras();
		ps.Add("user", WebUser.getNo());
		ps.Add("st", fk_station);
		
		if (DBAccess.IsExits("SELECT FK_Emp FROM Port_DeptEmpStation WHERE FK_Emp="
				+ SystemConfig.getAppCenterDBVarStr() + "user AND FK_Station="
				+ SystemConfig.getAppCenterDBVarStr() + "st", ps))
		{
			this.OpenAll();
		} else
		{
			this.Readonly();
		}
	}
	
	/**
	 * 仅仅对管理员
	 * @throws Exception 
	 */
	public final UAC OpenForSysAdmin() throws Exception
	{
		/*if (SystemConfig.getSysNo().equals("WebSite"))
		{
			this.OpenAll();
			return this;
		}*/
		
		if (WebUser.getNo().equals("admin"))
		{
			this.OpenAll();
		}
		return this;
	}
	
	public final UAC OpenForAppAdmin() throws Exception
	{
		if (WebUser.getNo() != null && WebUser.getNo().contains("admin"))
		{
			this.OpenAll();
		}
		return this;
	}
	
	// 控制属性
	/**
	 * 是否插入
	 */
	public boolean IsInsert = false;
	/**
	 * 是否删除
	 */
	public boolean IsDelete = false;
	/**
	 * 是否更新
	 */
	public boolean IsUpdate = false;
	/**
	 * 是否查看
	 */
	public boolean IsView = true;
	/**
	 * 附件
	 */
	public boolean IsAdjunct = false;
	
	 /**
	  * 是否可以导出
	  */
    public boolean IsExp = false;
    /**
     * 是否可以导入
     */
    public boolean IsImp = false;
	// 构造
	/**
	 * 用户访问
	 */
	public UAC()
	{
		
	}
}