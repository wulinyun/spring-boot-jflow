package BP.Sys;

import BP.DA.*;
import BP.En.*;

/** 
 SysEnumMain
 
*/
public class SysEnumMain extends EntityNoName
{
	public final String getCfgVal()
	{
		return this.GetValStrByKey(SysEnumMainAttr.CfgVal);
	}
	public final void setCfgVal(String value)
	{
		this.SetValByKey(SysEnumMainAttr.CfgVal, value);
	}
	public final String getLang()
	{
		return this.GetValStrByKey(SysEnumMainAttr.Lang);
	}
	public final void setLang(String value)
	{
		this.SetValByKey(SysEnumMainAttr.Lang, value);
	}
	/** 
	 SysEnumMain
	*/
	public SysEnumMain()
	{
	}
	/** 
	 SysEnumMain
	 @param no
	 * @throws Exception 
	*/
	public SysEnumMain(String no) throws Exception
	{
		try {
			try
			{
				this.setNo(no);
				this.Retrieve();
			}
			catch (RuntimeException ex)
			{
				SysEnums ses = new SysEnums(no);
				if (ses.size() == 0)
				{
					throw ex;
				}
				this.setNo(no);
				this.setName("未命名");
				String cfgVal = "";
				for (SysEnum item : ses.ToJavaList())
				{
					cfgVal += "@" + item.getIntKey() + "=" + item.getLab();
				}
				this.setCfgVal(cfgVal);
				this.Insert();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected boolean beforeDelete() throws Exception
	{
		// 检查这个类型是否被使用？
		MapAttrs attrs = new MapAttrs();
		QueryObject qo = new QueryObject(attrs);
		qo.AddWhere(MapAttrAttr.MyDataType, FieldTypeS.Enum.getValue());
		qo.addAnd();
		qo.AddWhere(MapAttrAttr.KeyOfEn, this.getNo());
		int i = qo.DoQuery();
		if (i == 0)
		{
			SysEnums ses = new SysEnums();
			ses.Delete(SysEnumAttr.EnumKey, this.getNo());
		}
		else
		{
			String msg = "错误:下列数据已经引用了枚举您不能删除它。"; // "错误:下列数据已经引用了枚举您不能删除它。";
			for (MapAttr attr : attrs.ToJavaList())
			{
				msg += "\t\n" + attr.getField() + "" + attr.getName() + " Table = " + attr.getFK_MapData();
			}

			//抛出异常，阻止删除.
			throw new RuntimeException(msg);
		}
		return super.beforeDelete();
	}
	/** 
	 Map
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("Sys_EnumMain", "枚举");
		map.Java_SetDepositaryOfEntity(Depositary.None);
		map.Java_SetDepositaryOfMap(Depositary.Application);
		map.Java_SetEnType(EnType.Sys);

		map.AddTBStringPK(SysEnumMainAttr.No, null, "编号", true, false, 1, 40, 8);
		map.AddTBString(SysEnumMainAttr.Name, null, "名称", true, false, 0, 40, 8);
		map.AddTBString(SysEnumMainAttr.CfgVal, null, "配置信息", true, false, 0, 1500, 8);
		map.AddTBString(SysEnumMainAttr.Lang, "CH", "语言", true, false, 0, 10, 8);
		this.set_enMap(map);
		return this.get_enMap();
	}

}