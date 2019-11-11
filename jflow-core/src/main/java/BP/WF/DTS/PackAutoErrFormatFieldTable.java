package BP.WF.DTS;

import BP.DA.DBAccess;
import BP.En.Method;
import BP.Sys.MapAttr;
import BP.Sys.MapAttrs;

/** 
 修复非法字段名称
 
*/
public class PackAutoErrFormatFieldTable extends Method
{
	/** 
	 修复非法字段名称
	 
	*/
	public PackAutoErrFormatFieldTable()
	{
		this.Title = "修复非法字段名称,物理表名称";
		this.Help = "在以前的版本中，用户创建表单物理表名、字段名的合法性没有检查会造成系统在自动创建物理表修复物理表时出现错误。此补丁可以批量修复全局的表单。";
		// this.Warning = "您确定要执行吗？";
		// this.HisAttrs.AddTBString("Path", "C:\\ccflow.Template", "生成的路径", true, false, 1, 1900, 200);
	}
	/** 
	 设置执行变量
	 
	 @return 
	*/
	@Override
	public void Init()
	{
	}
	/** 
	 当前的操纵员是否可以执行这个方法
	 
	*/
	@Override
	public boolean getIsCanDo()
	{
		return true;
	}
	/** 
	 执行
	 
	 @return 返回执行结果
	 * @throws Exception 
	*/
	@Override
	public Object Do() throws Exception
	{
		String keys = "~!@#$%^&*()+{}|:<>?`=[];,./～！＠＃￥％……＆×（）——＋｛｝｜：“《》？｀－＝［］；＇，．／";
		char[] cc = keys.toCharArray();
		for (char c : cc)
		{
			DBAccess.RunSQL("update sys_mapattr set keyofen=REPLACE(keyofen,'" + c + "' , '')");
		}

		MapAttrs attrs = new MapAttrs();
		attrs.RetrieveAll();
		int idx = 0;
		String msg = "";
		for (MapAttr item : attrs.ToJavaList())
		{
			String f = item.getKeyOfEn();
			try
			{
				int i = Integer.parseInt(item.getKeyOfEn().substring(0, 1));
				item.setKeyOfEn ("F" + item.getKeyOfEn());
				try
				{
					MapAttr itemCopy = new MapAttr();
					itemCopy.Copy(item);
					itemCopy.Insert();
					item.DirectDelete();
				}
				catch (RuntimeException ex)
				{
					msg += "@" + ex.getMessage();
				}
			}
			catch (Exception e)
			{
				continue;
			}
			DBAccess.RunSQL("UPDATE sys_mapAttr set KeyOfEn='"+item.getKeyOfEn()+"', mypk=FK_MapData+'_'+keyofen where keyofen='"+item.getKeyOfEn()+"'");
			msg += "@第(" + idx + ")个错误修复成功，原（"+f+"）修复成("+item.getKeyOfEn()+").";
			idx++;
		}

		DBAccess.RunSQL("UPDATE Sys_MapAttr SET MyPK=FK_MapData+'_'+KeyOfEn WHERE MyPK!=FK_MapData+'_'+KeyOfEn");
		return "修复信息如下:"+msg;
	}
}