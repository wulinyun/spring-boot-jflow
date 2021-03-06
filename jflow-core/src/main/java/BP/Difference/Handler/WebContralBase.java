package BP.Difference.Handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Enumeration;

import BP.Difference.ContextHolderUtils;
import org.apache.http.protocol.HttpContext;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;

import BP.DA.DBType;
import BP.DA.DataRow;
import BP.DA.DataSet;
import BP.DA.DataTable;
import BP.DA.DataType;
import BP.DA.Log;
import BP.En.Attr;
import BP.En.Attrs;
import BP.En.Entity;
import BP.En.FieldType;
import BP.Sys.Glo;
import BP.Sys.SystemConfig;
import BP.Sys.UIConfig;
import BP.Tools.StringHelper;
import BP.Web.WebUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class WebContralBase {
	/**
	 * 获得Form数据.
	 * 
	 * @param key
	 *            key
	 * @return 返回值
	 */
	public final String GetValFromFrmByKey(String key, String isNullAsVal) {
		String val = getRequest().getParameter(key);
		if (val == null && key.contains("DDL_") == false) {
			val = this.getRequest().getParameter("DDL_" + key);
		}

		if (val == null && key.contains("TB_") == false) {
			val = getRequest().getParameter("TB" + key);
		}

		if (val == null && key.contains("CB_") == false) {
			val = getRequest().getParameter("CB_" + key);
		}

		if (val == null) {
			if (isNullAsVal != null)
				return isNullAsVal;
            return "";
			//throw new RuntimeException("@获取Form参数错误,参数集合不包含[" + key + "]");
		}

		val = val.replace("'", "~");
		return val;
	}

	/**
	 * 获得Form数据.
	 * 
	 * @param key
	 *            key
	 * @return 返回值
	 */
	public final String GetValFromFrmByKey(String key) {
		String val = getRequest().getParameter(key);
		if (val == null && key.contains("DDL_") == false) {
			val = this.getRequest().getParameter("DDL_" + key);
		}

		if (val == null && key.contains("TB_") == false) {
			val = getRequest().getParameter("TB_" + key);
		}

		if (val == null && key.contains("CB_") == false) {
			val = getRequest().getParameter("CB_" + key);
		}

		if (val == null) {
			return "";
			//throw new RuntimeException("@获取Form参数错误,参数集合不包含[" + key + "]");
		}

		val = val.replace("'", "~");
		return val;
	}

	public float GetValFloatFromFrmByKey(String key) {
		String str = this.GetValFromFrmByKey(key);
		if (str == null || str == "")
			throw new RuntimeException("@参数:" + key + "没有取到值.");
		return Float.parseFloat(str);
	}

	public BigDecimal GetValDecimalFromFrmByKey(String key) {
		String str = this.GetValFromFrmByKey(key);
		if (str == null || str == "")
			throw new RuntimeException("@参数:" + key + "没有取到值.");
		return new BigDecimal(str);
	}

	public int getIndex() {
		String str = getRequest().getParameter("Index");
		if (str == null || str == "")
			return 1;
		return Integer.parseInt(str);
	}

	/**
	 * 执行方法
	 * 
	 * @param myEn
	 *            对象名
	 * @param methodName
	 *            方法
	 * @return 返回执行的结果，执行错误抛出异常
	 * @throws Exception
	 */
	public final String DoMethod(WebContralBase myEn, String methodName) throws Exception {

		Class tp = myEn.getClass();
		java.lang.reflect.Method mp = null;

		 
			mp = tp.getMethod(methodName);
		 

		if (mp == null) {
			/* 没有找到方法名字，就执行默认的方法. */
			String str = myEn.DoDefaultMethod();

			if (str == null || "".equals(str))
				return "err@方法:" + methodName + "没有翻译..";

			return str;
		}

		// 执行该方法.
		Object[] paras = null;
		Object tempVar = null;
		try {

			tempVar = mp.invoke(this, paras);

		} catch (Exception e) {

			String msg = null;

			if (e.getCause() != null) {
				msg = e.getCause().getMessage();
			}

			if (msg == null)
				msg = e.getMessage();

			// 如果有url返回.
			if (msg != null && (msg.indexOf("url@") == 0 || msg.indexOf("info@") == 0))
				return msg;

			String str = "";
			if (e.getCause() != null && e.getCause().getMessage().indexOf("wait") > -1) {
				str += "@错误原因可能是数据库连接异常";
			}

			String myParas = getRequest().getQueryString();

			String errInfo = "err@页面类[" + myEn.toString() + ",方法[" + methodName + "]执行错误.";
			errInfo += "\t\n@参数:" + myParas;
			errInfo += "\t\n@Msg:" + msg;
			errInfo += "\t\n@getStackTrace:" + e.getStackTrace();

			Log.DebugWriteError("BP.WF.HttpHangerBase.DoMethod()" + errInfo);
			return errInfo;

			// err @" +
			// e.getMessage()+str+"@"+e.getStackTrace()+"\t\n@"+myParas;

		}
		return (String) ((tempVar instanceof String) ? tempVar : null); // 调用由此
																		// MethodInfo
																		// 实例反射的方法或构造函数。
	}

	/**
	 * 执行默认的方法名称
	 * 
	 * @return 返回执行的结果
	 * @throws Exception
	 */
	protected String DoDefaultMethod() throws Exception {
		
		if (this.getDoType().contains(">")==true)
			return "err@非法的脚本植入";
		
		return "@子类没有重写该[" + this.getDoType() + "]方法.";
	}

	/**
	 * 公共方法获取值
	 * 
	 * @param param
	 *            参数名
	 * @return
	 */
	public final String GetRequestVal(String param) {

		String val = getRequest().getParameter(param);

		if (StringHelper.isNullOrEmpty(val)) {
			val = getRequest().getParameter(param);
		}

		return val;

	}

	public Boolean GetRequestValBoolen(String key) {
		if (this.GetRequestValInt(key) == 0)
			return false;

		return true;
	}

	/**
	 * 公共方法获取值
	 * 
	 * @param param
	 *            参数名
	 * @return
	 */
	public final int GetRequestValInt(String param) {
		String str = GetRequestVal(param);
		if (str == null || str.equals("") || str.equals("null")) {
			return 0;
		}
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 公共方法获取值
	 * 
	 * @param param
	 * @return
	 */
	public final long GetRequestValInt64(String param) {
		String str = GetRequestVal(param);
		if (str == null || str.equals("") || str.equals("null")) {
			return 0;
		}
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 数据
	 * 
	 * @param param
	 * @return
	 */
	public final float GetRequestValFloat(String param) {
		String str = GetRequestVal(param);
		if (str == null || str.equals("") || str.equals("null")) {
			return 0;
		}
		try {
			return Float.parseFloat(str);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 获得参数.
	 */
	public final String getRequestParas() {
		String urlExt = "";
		String rawUrl = this.getRequest().getQueryString();
		rawUrl = "&" + rawUrl.substring(rawUrl.indexOf('?') + 1);
		String[] paras = rawUrl.split("[&]", -1);
		for (String para : paras) {
			if (para == null || para.equals("") || para.contains("=") == false) {
				continue;
			}

			if (para.equals("1=1")) {
				continue;
			}

			urlExt += "&" + para;
		}
		return urlExt;
	}
	public final String getRequestParasOfAll() {
		String urlExt = "";
		String rawUrl = this.getRequest().getQueryString();
		rawUrl = "&" + rawUrl.substring(rawUrl.indexOf('?') + 1);
		String[] paras = rawUrl.split("[&]", -1);
		for (String para : paras) {
			
			if (para == null || para.equals("") || para.contains("=") == false) {
				continue;
			}

			if (para.equals("1=1")) {
				continue;
			}

			urlExt += "&" + para;
		}
		
		 Enumeration enu = getRequest().getParameterNames();
		while (enu.hasMoreElements())
		{
			
			String key = (String) enu.nextElement();
			 if (urlExt.contains(key+"=") == true)
              continue;
			
			 urlExt += "&" + key + "=" + getRequest().getParameter(key);
		}


		return urlExt;
	}

	/**
	 * 编号
	 */
	public final String getNo() {
		// String str = context.Request.QueryString["No"];
		String str = getRequest().getParameter("No");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;
	}

	public final String getFK_Dept() {
		String str = getRequest().getParameter("FK_Dept");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;
	}
	

	public final String getName() {
		String str = getRequest().getParameter("Name");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;
	}

	/**
	 * 执行类型
	 */
	public final String getDoType() {
		// 获得执行的方法.
		String doType = "";

		doType = this.GetRequestVal("DoType");
		if (doType == null) {
			doType = this.GetRequestVal("Action");
		}

		if (doType == null) {
			doType = this.GetRequestVal("action");
		}

		if (doType == null) {
			doType = this.GetRequestVal("Method");
		}

		return doType;
	}

	public final String getEnsName() {
		String str = this.GetRequestVal("EnsName");

		if (str == null || str.equals("") || str.equals("null")) {
			str = this.GetRequestVal("FK_MapData");
		}
		
		if (str == null || str.equals("") || str.equals("null")) {
			str = this.GetRequestVal("FrmID");
		}

		if (str == null || str.equals("") || str.equals("null")) {
			if (this.getEnName() == null)
                return null;
            return this.getEnName() + "s";
		}

		return str;
	}
	
	public final String getTreeEnsName() {
		String str = this.GetRequestVal("TreeEnsName");

		if (str == null || str.equals("") || str.equals("null")) {
			if (this.getEnName() == null)
                return null;
            return this.getEnName() + "s";
		}

		return str;
	}
	
	public final String getMyPK() {
		String str = this.GetRequestVal("MyPK");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;
	}

	public final String getFK_Event() {
		String str = this.GetRequestVal("FK_Event");
		if (str == null || str.equals("") || str.equals("null"))
			return null;
		return str;
	}

	/**
	 * 字典表
	 */
	public final String getFK_SFTable() {
		String str = this.GetRequestVal("FK_SFTable");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;
	}

	public final String getEnumKey() {
		String str = this.GetRequestVal("EnumKey");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;
	}

	public final String getKeyOfEn() {
		String str = this.GetRequestVal("KeyOfEn");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;

	}

	/**
	 * FK_MapData
	 */
	public final String getFK_MapData() {
		String str = this.GetRequestVal("FK_MapData");
		if (str == null || str.equals("") || str.equals("null")) {
			str = this.GetRequestVal("FrmID");
		}
		return str;

	}

	/**
	 * 扩展信息
	 */
	public final String getFK_MapExt() {
		String str = this.GetRequestVal("FK_MapExt");
		if (StringHelper.isNullOrEmpty(str)) {
			str = this.GetRequestVal("MyPK");
			if (StringHelper.isNullOrEmpty(str)) {
				return null;
			}
		}
		return str;
	}

	/**
	 * 流程编号
	 */
	public final String getFK_Flow() {
		String str = this.GetRequestVal("FK_Flow");
		if ( DataType.IsNullOrEmpty(str)) {
			return null;
		}
		return str;
	}
	
	public final String getPFlowNo() {
		String str = this.GetRequestVal("PFlowNo");
		if ( DataType.IsNullOrEmpty(str)) {
			return null;
		}
		return str;
	}
	 

	public final int getPNodeID() {
		String str = this.GetRequestVal("PNodeID");
		if (DataType.IsNullOrEmpty(str)) {
			return 0;
		}

		return Integer.parseInt(str);
	}
	
	 
	
	public final long getPFID() { 
		String str = getRequest().getParameter("PFID");
		if (str == null || str.equals("") || str.equals("null")) {
			return 0;
		}
		return Integer.parseInt(str);
	}

	public final int getGroupField() {
		String str = this.GetRequestVal("GroupField");
		if (DataType.IsNullOrEmpty(str)) {
			return 0;
		}

		return Integer.parseInt(str);
	}
	
	/**
	 * 节点ID
	 */
	public int getFK_Node() {
		// return this.GetRequestValInt("FK_Node");
		int nodeID = this.GetRequestValInt("FK_Node");
		if (nodeID == 0)
			nodeID = this.GetRequestValInt("NodeID");
		return nodeID;
	}

	public final long getFID() { 
		String str = getRequest().getParameter("FID");
		if (str == null || str.equals("") || str.equals("null")) {
			return 0;
		}
		return Integer.parseInt(str);
	}

	public long getWorkID() {

		String str = this.GetRequestVal("WorkID");
		if (str == null || str.equals("") || str.equals("null"))
			str = this.GetRequestVal("PKVal");

		if (str == null || str.equals("") || str.equals("null"))
			str = this.GetRequestVal("OID");

		if (str == null || str.equals("") || str.equals("null"))
			return 0;

		return Integer.parseInt(str);
	}

	/**
	 * 框架ID
	 */
	public final String getFK_MapFrame() {
		// String str = context.Request.QueryString["FK_MapFrame"];
		String str = getRequest().getParameter("FK_MapFrame");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;
	}

	/**
	 * RefOID
	 */
	public final int getRefOID() {
		// String str = context.Request.QueryString["RefOID"];
		String str = getRequest().getParameter("RefOID");

		if (str == null || str.equals("") || str.equals("null")) {
			// str = context.Request.QueryString["OID"];
			str = getRequest().getParameter("OID");
		}

		if (str == null || str.equals("") || str.equals("null")) {
			return 0;
		}

		return Integer.parseInt(str);
	}

	/**
	 * 明细表
	 */

	public final String getFK_MapDtl() {
		String str = getRequest().getParameter("FK_MapDtl");
		if (str == null || str.equals("") || str.equals("null")) {
			str = getRequest().getParameter("EnsName");
		}
		return str;
	}

	/**
	 * 页面Index. /** 字段属性编号
	 */
	public final String getAth() {
		// String str = context.Request.QueryString["Ath"];
		String str = getRequest().getParameter("Ath");
		if (str == null || str.equals("") || str.equals("null")) {
			return null;
		}
		return str;
	}

	public HttpContext context = null;

	/**
	 * 获得Int数据
	 * 
	 * @param key
	 * @return
	 */
	public final int GetValIntFromFrmByKey(String key) {
		String str = this.GetValFromFrmByKey(key);
		if (str == null || str.equals("") || str.equals("0")) {
			throw new RuntimeException("@参数:" + key + "没有取到值.");
		}
		return Integer.parseInt(str);
	}

	public final boolean GetValBoolenFromFrmByKey(String key) {
		String val = this.GetValFromFrmByKey(key, "0");
		if (val == "on" || val == "1")
			return true;
		if (val == null || val.equals("") || val.equals("0")) {
			return false;
		}
		return true;
	}

	public final String getRefPK() {
		// String str = this.context.Request.QueryString["RefPK"];
		String str = getRequest().getParameter("RefPK");
		return str;
	}

	public final String getRefPKVal() {
		// String str = this.context.Request.QueryString["RefPKVal"];
		String str = this.getRequest().getParameter("RefPKVal");
		if (str == null) {
			return "1";
		}
		return str;
	}

	/*
	 * 表单ID
	 */
	public final String getFrmID() {
		String str = this.GetRequestVal("FrmID");
		if (str == null || str.equals("") || str.equals("null")) {
			return this.GetRequestVal("FK_MapData");
		}

		return str;
	}

	private long WorkID;

	public long getPWorkID() {

		return this.GetRequestValInt("PWorkID");
	}

	public final long getCWorkID() {
		return this.GetRequestValInt("CWorkID");
	}

	/// #region 属性参数.
	/**
	 * @于庆海翻译.
	 * 
	 */
	public String getPKVal() {
		String str = this.GetRequestVal("PKVal");

		if (DataType.IsNullOrEmpty(str) == true) {
			str = this.GetRequestVal("OID");
		}

		if (DataType.IsNullOrEmpty(str) == true) {
			str = this.GetRequestVal("No");
		}

		if (DataType.IsNullOrEmpty(str) == true) {
			str = this.GetRequestVal("MyPK");
		}
		if (DataType.IsNullOrEmpty(str) == true) {
			str = this.GetRequestVal("NodeID");
		}

		if (DataType.IsNullOrEmpty(str) == true) {
			str = this.GetRequestVal("WorkID");
		}

		if ("null".equals(str) == true)
			return null;

		return str;
	}

	/// 是否是移动？
	/// </summary>
	public boolean getIsMobile() {
		String v = this.GetRequestVal("IsMobile");
		if (v != null && v == "1")
			return true;

		if (Glo.getRequest().getRequestURI().contains("/CCMobile/"))
			return true;

		return false;
	}
	
	protected String ExportDGToExcel(DataSet ds,  String title,String params) throws Exception {
		
		DataTable dt = ds.GetTableByName("GroupSearch");
		DataTable AttrsOfNum = ds.GetTableByName("AttrsOfNum");
		DataTable AttrsOfGroup = ds.GetTableByName("AttrsOfGroup"); 		

		String fileName = title+"Ep" + title + ".xls";
		String fileDir = SystemConfig.getPathOfTemp();
		String filePth = SystemConfig.getPathOfTemp();
		// 参数及变量设置
		// 如果导出目录没有建立，则建立.
		File file = new File(fileDir);
		if (!file.exists()) {
			file.mkdirs();
		}

		filePth = filePth + "/" + fileName;
		file = new File(filePth);
		if (file.exists()) {
			file.delete();
		}

		// String httpFilePath =
		// Glo.getCCFlowAppPath()+"DataUser/Temp/"+fileName;
		int headerRowIndex = 0; // 文件标题行序
		int titleRowIndex = 1; // 列标题行序
		int countCell = 0;// 显示的列数
		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet(title+"Ep" + title);
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = null;
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		HSSFFont font = null;
		HSSFDataFormat fmt = wb.createDataFormat();
		HSSFCell cell = null;

		// 生成标题

		row = sheet.createRow((int) titleRowIndex);
		int index = 0;// 控制列 qin 15.9.21
		//添加序号
		cell = row.createCell(index);
		cell.setCellStyle(style);
		cell.setCellValue("序号");
		index += 1;
		countCell++;
		for (DataRow attr : AttrsOfGroup.Rows) {
			cell = row.createCell(index);
			cell.setCellStyle(style);
			cell.setCellValue(attr.getValue("Name").toString());
			index += 1;
			countCell++;
		}
		for (DataRow attr : AttrsOfNum.Rows) {
			cell = row.createCell(index);
			cell.setCellStyle(style);
			cell.setCellValue(attr.getValue("Name").toString());
			index += 1;
			countCell++;
		}
		DataRow dr = null;
		for (int i = 2; i <= dt.Rows.size() + 1; i++) {
			dr = dt.Rows.get(i - 2);
			row = sheet.createRow(i);
			// 生成文件内容
			index = 0;
			cell = row.createCell(index);
			cell.setCellStyle(style);
			cell.setCellValue(dr.getValue("IDX").toString());
			index += 1;
			for (DataRow attr : AttrsOfGroup.Rows) {
				
				cell = row.createCell(index);
				cell.setCellStyle(style);
				cell.setCellValue(dr.getValue(attr.getValue("KeyOfEn")+"T").toString());
				index += 1;
			}
			for (DataRow attr : AttrsOfNum.Rows) {
							
				cell = row.createCell(index);
				cell.setCellStyle(style);
				cell.setCellValue(dr.getValue(attr.getValue("KeyOfEn").toString()).toString());
				index += 1;
			}

		}
		int creatorRowIndex = titleRowIndex + dt.Rows.size() + 1;

		row = sheet.createRow((int) creatorRowIndex);
		
		// 生成文件内容
		index = 0;
		cell = row.createCell(index);
		cell.setCellStyle(style);
		cell.setCellValue("汇总");
		index += 1;
		for (DataRow attr : AttrsOfGroup.Rows) {
			
			cell = row.createCell(index);
			cell.setCellStyle(style);
			cell.setCellValue("");
			index += 1;
		}

		for (DataRow attr : AttrsOfNum.Rows) {
			double d =0;
			cell = row.createCell(index);
			cell.setCellStyle(style);
			for(DataRow dtr : dt.Rows){
				d += Double.parseDouble(dtr.getValue(attr.getValue("KeyOfEn").toString()).toString());
			}
			if(params.contains(attr.getValue("KeyOfEn")+"=AVG")){
				if(dt.Rows.size()!=0){
					DecimalFormat df = new DecimalFormat("#.0000");            
					d = Double.valueOf(df.format(d/dt.Rows.size()));
				}
					
			}
			
			if(Integer.parseInt(attr.getValue("MyDataType").toString()) == DataType.AppInt){
				if(params.contains(attr.getValue("KeyOfEn")+"=AVG"))
					cell.setCellValue(d);
				else
					cell.setCellValue((int)d);
			}else{
				cell.setCellValue(d);
			}
			
			index += 1;
		}
		

		// 列标题单元格样式设定
		HSSFCellStyle titleStyle = wb.createCellStyle();
		/*
		 * titleStyle.setBorderTop(HSSFBorderFormatting.BORDER_THIN);
		 * titleStyle.setBorderBottom(HSSFBorderFormatting.BORDER_THIN);
		 * titleStyle.setBorderLeft(HSSFBorderFormatting.BORDER_THIN);
		 * titleStyle.setBorderRight(HSSFBorderFormatting.BORDER_THIN);
		 */
		titleStyle.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
		titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		font = wb.createFont();
		font.setBold(true);
		titleStyle.setFont(font);
		row = sheet.createRow((int) 0);
		sheet.addMergedRegion(new Region(headerRowIndex, (short) headerRowIndex, 0, (short) (countCell - 1)));
		cell = row.createCell(headerRowIndex);
		cell.setCellValue(title);
		cell.setCellStyle(titleStyle);

		// 生成制单人
		// 制表人单元格样式设定
		HSSFCellStyle userStyle = wb.createCellStyle();
		userStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		userStyle.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
		creatorRowIndex = creatorRowIndex+1;

		row = sheet.createRow((int) creatorRowIndex);

		sheet.addMergedRegion(new Region(creatorRowIndex, (short) 0, creatorRowIndex, (short) (countCell - 1)));
		cell = row.createCell(0);
		cell.setCellValue("制表人：" + WebUser.getName() + "日期：" + DataType.getCurrentDataTimeCNOfShort());
		cell.setCellStyle(userStyle);
		// 第六步，将文件存到指定位置
		try {
			FileOutputStream fout = new FileOutputStream(filePth);
			wb.write(fout);
			fout.flush();
			fout.close();
			return "/DataUser/Temp/" + fileName;
		} catch (Exception e) {
			e.printStackTrace();
			return fileName;
		}

	}
	
	protected String ExportDGToExcel(DataTable dt, Entity en, String title, Attrs mapAttrs) throws Exception {

		String fileName = title + "_" + DataType.getCurrentDataCNOfLong() + "_" + WebUser.getNo() + ".xls";
		String fileDir = SystemConfig.getPathOfTemp();
		String filePth = SystemConfig.getPathOfTemp();
		// 参数及变量设置
		// 如果导出目录没有建立，则建立.
		File file = new File(fileDir);
		if (!file.exists()) {
			file.mkdirs();
		}

		filePth = filePth + "/" + fileName;
		file = new File(filePth);
		if (file.exists()) {
			file.delete();
		}

		int headerRowIndex = 0; // 文件标题行序
		int titleRowIndex = 1; // 列标题行序
		int countCell = 0;// 显示的列数
		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet(en.getEnMap().getPhysicsTable());
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = null;
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		HSSFFont font = null;
		HSSFDataFormat fmt = wb.createDataFormat();
		HSSFCell cell = null;

		// 生成标题
		Attrs attrs = null;
		if(mapAttrs!=null)
			attrs = mapAttrs;
		else
			attrs = en.getEnMap().getAttrs();
		Attrs selectedAttrs = null;
		UIConfig cfg = new UIConfig(en);
		if (cfg.getShowColumns().length == 0)
			selectedAttrs = attrs;
		else {
			selectedAttrs = new Attrs();

			for (Attr attr : attrs) {

				boolean contain = false;

				for (String col : cfg.getShowColumns()) {
					if (col.equals(attr.getKey())) {
						contain = true;
						break;
					}
				}

				if (contain)
					selectedAttrs.Add(attr);
			}
		}
		row = sheet.createRow((int) titleRowIndex);
		int index = 0;// 控制列 qin 15.9.21
		for (int i = 0; i < selectedAttrs.size(); i++) {
			Attr attr = selectedAttrs.get(i);
			if (attr.getKey().equals("MyNum"))
				continue;
			if (attr.getKey().equals("OID"))
				continue;
			
			if (attr.getUIVisible() == false && attr.getMyFieldType() != FieldType.RefText)
				continue;
			
			 if (attr.getIsFKorEnum())
                 continue;
             if (attr.getKey().equals("MyFilePath") || attr.getKey().equals("MyFileExt") 
                 || attr.getKey().equals("WebPath") || attr.getKey().equals("MyFileH")
                 || attr.getKey().equals("MyFileW") || attr.getKey().equals("MyFileSize"))
                 continue;
             
             cell = row.createCell(index);
 			 cell.setCellStyle(style);
             if(attr.getMyFieldType() == FieldType.RefText)
            	 cell.setCellValue(attr.getDesc().replace("名称",""));  
             else
            	 cell.setCellValue(attr.getDesc());
			index += 1;
			countCell++;
		}
		DataRow dr = null;
		for (int i = 2; i <= dt.Rows.size() + 1; i++) {
			dr = dt.Rows.get(i - 2);
			row = sheet.createRow(i);
			// 生成文件内容
			index = 0;
			for (int j = 0; j < selectedAttrs.size(); j++) {
				Attr attr = selectedAttrs.get(j);
				if (attr.getKey().equals("MyNum"))
					continue;
				if (attr.getKey().equals("OID"))
					continue;
				 if (attr.getIsFKorEnum())
                     continue;
				
				if (attr.getUIVisible() == false && attr.getMyFieldType() != FieldType.RefText)
					continue;
				
	             if (attr.getKey().equals("MyFilePath") || attr.getKey().equals("MyFileExt") 
	                 || attr.getKey().equals("WebPath") || attr.getKey().equals("MyFileH")
	                 || attr.getKey().equals("MyFileW") || attr.getKey().equals("MyFileSize"))
	                 continue;
	             
				
				String str = "";
				if (attr.getMyDataType() == DataType.AppBoolean) {
					if (SystemConfig.getAppCenterDBType() == DBType.Oracle)
						str = dr.get(attr.getKey().toUpperCase()).equals(1) ? "是" : "否";
					else
						str = dr.get(attr.getKey()).equals(1) ? "是" : "否";
				} else {
					String text ="";
					if (SystemConfig.getAppCenterDBType() == DBType.Oracle){
						Object obj = dr.get((attr.getIsFKorEnum() ? (attr.getKey() + "Text") : attr.getKey()).toUpperCase());
						if(obj == null)
							text = "";
						else
							text =  obj.toString();
					}else{
						Object obj = dr.get(attr.getIsFKorEnum() ? (attr.getKey() + "Text") : attr.getKey());
						if(obj == null)
							text = "";
						else
							text =  obj.toString();
					}
					
					if(DataType.IsNullOrEmpty(text)==false && (text.contains("\n")==true ||text.contains("\r")==true)){
						str =""+text.replaceAll("\n", "  ");
					    str =""+text.replaceAll("\r", "  ");
					}else{
						str = text;
					}
				}
				if (str == null || str.equals("") || str.equals("null")) {
					str = " ";
				}
				cell = row.createCell(index);
				cell.setCellStyle(style);
				cell.setCellValue(str);
				index += 1;
			}

		}

		// 列标题单元格样式设定
		HSSFCellStyle titleStyle = wb.createCellStyle();
		/*
		 * titleStyle.setBorderTop(HSSFBorderFormatting.BORDER_THIN);
		 * titleStyle.setBorderBottom(HSSFBorderFormatting.BORDER_THIN);
		 * titleStyle.setBorderLeft(HSSFBorderFormatting.BORDER_THIN);
		 * titleStyle.setBorderRight(HSSFBorderFormatting.BORDER_THIN);
		 */
		titleStyle.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
		titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		font = wb.createFont();
		font.setBold(true);
		titleStyle.setFont(font);
		row = sheet.createRow((int) 0);
		sheet.addMergedRegion(new Region(headerRowIndex, (short) headerRowIndex, 0, (short) (countCell - 1)));
		cell = row.createCell(headerRowIndex);
		cell.setCellValue(title);
		cell.setCellStyle(titleStyle);

		// 生成制单人
		// 制表人单元格样式设定
		HSSFCellStyle userStyle = wb.createCellStyle();
		userStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		userStyle.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
		int creatorRowIndex = titleRowIndex + dt.Rows.size() + 1;

		row = sheet.createRow((int) creatorRowIndex);

		sheet.addMergedRegion(new Region(creatorRowIndex, (short) 0, creatorRowIndex, (short) (countCell - 1)));
		cell = row.createCell(0);
		cell.setCellValue("制表人：" + WebUser.getName() + "日期：" + DataType.getCurrentDataTimeCNOfShort());
		cell.setCellStyle(userStyle);
		// 第六步，将文件存到指定位置
		try {
			FileOutputStream fout = new FileOutputStream(filePth);
			wb.write(fout);
			fout.flush();
			fout.close();
			return "/DataUser/Temp/" + fileName;
		} catch (Exception e) {
			e.printStackTrace();
			return fileName;
		}

	}

	/*
	 * public static String DataTableToExcel(DataTable dt, String filename,
	 * String header, String creator, boolean date, boolean index, boolean
	 * download) {
	 * 
	 * String file = BP.Sys.SystemConfig.getPathOfTemp() + filename;
	 * 
	 * String dir = BP.Sys.SystemConfig.getPathOfTemp(); String name = filename;
	 * long len = 0; HSSFRow row = null, headerRow = null, dateRow = null,
	 * sumRow = null, creatorRow = null; HSSFCell cell = null; int r = 0; int c
	 * = 0; int headerRowIndex = 0; //文件标题行序 int dateRowIndex = 0; //日期行序 int
	 * titleRowIndex = 0; //列标题行序 int sumRowIndex = 0; //合计行序 int
	 * creatorRowIndex = 0; //创建人行序 float DEF_ROW_HEIGHT = 20; //默认行高 float
	 * charWidth = 0; //单个字符宽度 int columnWidth = 0; //列宽，像素 boolean isDate;
	 * //是否是日期格式，否则是日期时间格式 int decimalPlaces = 2; //小数位数 boolean qian;
	 * //是否使用千位分隔符 ArrayList sumColumns = new ArrayList(); //合计列序号集合 File files
	 * = new File(dir); if (files.exists() == false) files.mkdirs();
	 * 
	 * 
	 * /* //一个字符的像素宽度，以Arial，10磅，i进行测算 Bitmap bmp = new Bitmap(10, 10); using ()
	 * { using (Graphics g = Graphics.FromImage(bmp)) { charWidth =
	 * g.MeasureString("i", new Font("Arial", 10)).Width; } }
	 */
	// 序
	/*
	 * if (index && dt.Columns.contains("序") == false) {
	 * dt.Columns.Add("序").ExtendedProperties.Add("width", 50);
	 * dt.Columns.get("序").setOrdinal(0);
	 * 
	 * for (int i = 0; i < dt.Rows.size(); i++) dt.Rows.get(i).setValue("序", i +
	 * 1); } //合计列 for (DataColumn col : dt.Columns) { if
	 * (col.ExtendedProperties.ContainsKey("sum") == false) continue;
	 * 
	 * sumColumns.add(col.getOrdinal()); }
	 * 
	 * headerRowIndex = StringHelper.isNullOrEmpty(header) ? -1 : 0;
	 * dateRowIndex = date ? (headerRowIndex + 1) : -1; titleRowIndex = date ?
	 * dateRowIndex + 1 : headerRowIndex == -1 ? 0 : 1; sumRowIndex =
	 * sumColumns.size() == 0 ? -1 : titleRowIndex + dt.Rows.size() + 1;
	 * creatorRowIndex = StringHelper.isNullOrEmpty(creator) ? -1 : sumRowIndex
	 * == -1 ? titleRowIndex + dt.Rows.size() + 1 : sumRowIndex + 1;
	 * 
	 * FileInputStream fs = new FileInputStream(file); HSSFWorkbook wb = new
	 * HSSFWorkbook(); HSSFSheet sheet = wb.createSheet("Sheet1");
	 * sheet.setDefaultRowHeightInPoints(DEF_ROW_HEIGHT); HSSFFont font = null;
	 * HSSFDataFormat fmt = wb.createDataFormat();
	 * 
	 * if (headerRowIndex != -1) headerRow = sheet.createRow(headerRowIndex); if
	 * (date) dateRow = sheet.createRow(dateRowIndex); if (sumRowIndex != -1)
	 * sumRow = sheet.createRow(sumRowIndex); if (creatorRowIndex != -1)
	 * creatorRow = sheet.createRow(creatorRowIndex);
	 * 
	 * ///#region 单元格样式定义 //列标题单元格样式设定 HSSFCellStyle titleStyle =
	 * wb.createCellStyle();
	 * titleStyle.setBorderTop(HSSFBorderFormatting.BORDER_THIN);
	 * titleStyle.setBorderBottom(HSSFBorderFormatting.BORDER_THIN);
	 * titleStyle.setBorderLeft(HSSFBorderFormatting.BORDER_THIN);
	 * titleStyle.setBorderRight(HSSFBorderFormatting.BORDER_THIN);
	 * titleStyle.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER); font =
	 * wb.createFont(); font.setBold(true); titleStyle.setFont(font);
	 * 
	 * //“序”列标题样式设定 HSSFCellStyle idxTitleStyle = wb.createCellStyle();
	 * idxTitleStyle.cloneStyleFrom(titleStyle);
	 * idxTitleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	 * 
	 * //文件标题单元格样式设定 HSSFCellStyle headerStyle = wb.createCellStyle();
	 * headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	 * headerStyle.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER); font =
	 * wb.createFont(); font.setFontHeightInPoints((short)12);
	 * font.setBold(true); headerStyle.setFont(font);
	 * 
	 * //制表人单元格样式设定 HSSFCellStyle userStyle = wb.createCellStyle();
	 * userStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
	 * userStyle.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
	 * 
	 * //单元格样式设定 HSSFCellStyle cellStyle = wb.createCellStyle();
	 * cellStyle.setBorderTop(HSSFBorderFormatting.BORDER_THIN);
	 * cellStyle.setBorderBottom(HSSFBorderFormatting.BORDER_THIN);
	 * cellStyle.setBorderLeft(HSSFBorderFormatting.BORDER_THIN);
	 * cellStyle.setBorderRight(HSSFBorderFormatting.BORDER_THIN);
	 * cellStyle.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
	 * 
	 * //数字单元格样式设定 HSSFCellStyle numCellStyle = wb.createCellStyle();
	 * numCellStyle.cloneStyleFrom(cellStyle);
	 * numCellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
	 * 
	 * //“序”列单元格样式设定 HSSFCellStyle idxCellStyle = wb.createCellStyle();
	 * idxCellStyle.cloneStyleFrom(cellStyle);
	 * idxCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	 * 
	 * //日期单元格样式设定 HSSFCellStyle dateCellStyle = wb.createCellStyle();
	 * dateCellStyle.cloneStyleFrom(cellStyle);
	 * dateCellStyle.setDataFormat(fmt.getFormat("yyyy-m-d;@"));
	 * 
	 * //日期时间单元格样式设定 HSSFCellStyle timeCellStyle = wb.createCellStyle();
	 * timeCellStyle.cloneStyleFrom(cellStyle);
	 * timeCellStyle.setDataFormat(fmt.getFormat("yyyy-m-d h:mm;@"));
	 * 
	 * //千分位单元格样式设定 HSSFCellStyle qCellStyle = wb.createCellStyle();
	 * qCellStyle.cloneStyleFrom(cellStyle);
	 * qCellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
	 * qCellStyle.setDataFormat(fmt.getFormat("#,##0_ ;@"));
	 * 
	 * //小数点、千分位单元格样式设定 Dictionary<String, HSSFCellStyle> cstyles = new
	 * Dictionary<String, HSSFCellStyle>(); HSSFCellStyle cstyle = null;
	 * ///#endregion
	 * 
	 * //输出列标题 row = sheet.createRow(titleRowIndex);
	 * row.setHeightInPoints(DEF_ROW_HEIGHT);
	 * 
	 * for (DataColumn col : dt.Columns) { cell = row.createCell(c++);
	 * cell.setCellValue(col.ColumnName); cell.setCellStyle(col.ColumnName ==
	 * "序" ? idxTitleStyle : titleStyle);
	 * 
	 * columnWidth = col.ExtendedProperties.ContainsKey("width") ?
	 * ((Integer)col.ExtendedProperties.get("width")).intValue() : 100;
	 * sheet.setColumnWidth(c - 1, (int)(Math.ceil(columnWidth / charWidth) +
	 * 0.72) * 256);
	 * 
	 * if (headerRow != null) headerRow.createCell(c - 1); if (dateRow != null)
	 * dateRow.createCell(c - 1); if (sumRow != null) sumRow.createCell(c - 1);
	 * if (creatorRow != null) creatorRow.createCell(c - 1);
	 * 
	 * //定义数字列单元格样式 switch (col.DataType.Name) { case "Single": case "Double":
	 * case "Decimal": decimalPlaces =
	 * col.ExtendedProperties.ContainsKey("dots") ?
	 * ((Integer)col.ExtendedProperties.get("dots")).intValue() : 2; qian =
	 * col.ExtendedProperties.ContainsKey("k") ?
	 * (Boolean)col.ExtendedProperties.get("k") : false;
	 * 
	 * if (decimalPlaces > 0 && !qian) { cstyle = wb.createCellStyle();
	 * cstyle.cloneStyleFrom(qCellStyle);
	 * cstyle.setDataFormat(fmt.getFormat("0." +
	 * StringHelper.padLeft(decimalPlaces, '0') + "_ ;@")); } else if
	 * (decimalPlaces == 0 && qian) { cstyle = wb.createCellStyle();
	 * cstyle.cloneStyleFrom(qCellStyle); } else if (decimalPlaces > 0 && qian)
	 * { cstyle = wb.createCellStyle(); cstyle.cloneStyleFrom(qCellStyle);
	 * cstyle.setDataFormat(fmt.GetFormat("#,##0." +
	 * StringHelper.padLeft(decimalPlaces, '0') + "_ ;@")); }
	 * 
	 * cstyles.put(col.ColumnName, cstyle); break; default: break; }
	 * 
	 * //输出文件标题 if (headerRow != null) { sheet.addMergedRegion(new
	 * Region(headerRowIndex, (short)headerRowIndex, 0,
	 * (short)(dt.Columns.size() - 1))); cell = headerRow.getCell(0);
	 * cell.setCellValue(header); cell.setCellStyle(headerStyle);
	 * headerRow.setHeightInPoints(26); } //输出日期 if (dateRow != null) {
	 * sheet.addMergedRegion(new Region(dateRowIndex, (short)dateRowIndex, 0,
	 * (short)( dt.Columns.size() - 1))); cell = dateRow.getCell(0);
	 * cell.SetCellValue("日期：" + DateTime.Today.ToString("yyyy-MM-dd"));
	 * cell.setCellStyle(userStyle); dateRow.setHeightInPoints(DEF_ROW_HEIGHT);
	 * } //输出制表人 if (creatorRow != null) { sheet.addMergedRegion(new
	 * Region(creatorRowIndex, (short)creatorRowIndex, 0,
	 * (short)(dt.Columns.size() - 1))); cell = creatorRow.getCell(0);
	 * cell.setCellValue("制表人：" + creator); cell.setCellStyle(userStyle);
	 * creatorRow.setHeightInPoints(DEF_ROW_HEIGHT); }
	 * 
	 * r = titleRowIndex + 1; //输出查询结果 for (DataRow dr : dt.Rows) { row =
	 * sheet.createRow(r++); row.setHeightInPoints(DEF_ROW_HEIGHT); c = 0;
	 * 
	 * for(DataColumn col: dt.Columns) { cell = row.createCell(c++);
	 * 
	 * switch (col.DataType.Name) { case "Boolean":
	 * cell.setCellStyle(cellStyle);
	 * cell.setCellValue(dr.get(col.ColumnName).equals(true) ? "是" : "否");
	 * break; case "DateTime": isDate =
	 * col.ExtendedProperties.ContainsKey("isdate") ?
	 * (Boolean)col.ExtendedProperties.get("isdate") : false;
	 * 
	 * cell.setCellStyle(isDate ? dateCellStyle : timeCellStyle);
	 * cell.setCellValue(String.valueOf(dr.get(col.ColumnName))); break; case
	 * "Int16": case "Int32": case "Int64": qian =
	 * col.ExtendedProperties.ContainsKey("k") ?
	 * (Boolean)col.ExtendedProperties.get("k") : false;
	 * 
	 * cell.setCellStyle(col.ColumnName == "序" ? idxCellStyle : qian ?
	 * qCellStyle : numCellStyle);
	 * cell.setCellValue(((Long)dr.get(col.ColumnName))); break; case "Single":
	 * case "Double": case "Decimal":
	 * cell.setCellStyle(cstyles.get(col.ColumnName));
	 * cell.setCellValue(((Double)dr.get(col.ColumnName))); break; default:
	 * cell.setCellStyle(cellStyle);
	 * cell.setCellValue(String.valueOf(dr.get(col.ColumnName))); break; } } }
	 * //合计 if (sumRow != null) { sumRow.setHeightInPoints(DEF_ROW_HEIGHT);
	 * 
	 * for (c = 0; c < dt.Columns.size(); c++) { cell = sumRow.getCell(c);
	 * cell.setCellStyle(cellStyle);
	 * 
	 * if (sumColumns.contains(c) == false) continue;
	 * 
	 * cell.SetCellFormula(string.Format("SUM({0}:{1})", GetCellName(c,
	 * titleRowIndex + 1), GetCellName(c, titleRowIndex + dt.Rows.size()))); } }
	 * 
	 * wb.write(fs); len = fs; }
	 * 
	 * 
	 * return null; }
	 */

	/// <summary>
	/// 获取单元格的显示名称，格式如A1,B2
	/// </summary>
	/// <param name="columnIdx">单元格列号</param>
	/// <param name="rowIdx">单元格行号</param>
	/// <returns></returns>
	public static String GetCellName(int columnIdx, int rowIdx) {
		int[] maxs = new int[] { 26, 26 * 26 + 26, 26 * 26 * 26 + (26 * 26 + 26) + 26 };
		int col = columnIdx + 1;
		int row = rowIdx + 1;

		if (col > maxs[2])

			return "列序号不正确，超出最大值";

		int alphaCount = 1;

		for (int m : maxs) {
			if (m < col)
				alphaCount++;
		}

		switch (alphaCount) {
		case 1:
			return (char) (col + 64) + "" + row;
		case 2:
			return (char) ((col / 26) + 64) + "" + (char) ((col % 26) + 64) + row;
		case 3:
			return (char) ((col / 26 / 26) + 64) + "" + (char) (((col - col / 26 / 26 * 26 * 26) / 26) + 64) + ""
					+ (char) ((col % 26) + 64) + row;
		}

		return "Unkown";
	}

	// BaseController

	public HttpServletRequest getRequest() {
		return ContextHolderUtils.getRequest();
	}

	public HttpServletResponse getResponse() {
		return ContextHolderUtils.getResponse();
	}

	public String getParamter(String key){
		return getRequest().getParameter(key);
	}

	/**
	 * 增加列的数量。
	 */
	public final int getaddRowNum() {
		try {
			int i = Integer.parseInt(ContextHolderUtils.getRequest()
					.getParameter("addRowNum"));
			if (ContextHolderUtils.getRequest().getParameter("IsCut") == null) {
				return i;
			} else {
				return i;
			}
		} catch (Exception e) {
			return 0;
		}
	}

	public final int getIsWap() {
		if(ContextHolderUtils.getRequest().getParameter("IsWap")==null)
			return 0;
		if (ContextHolderUtils.getRequest().getParameter("IsWap").equals("1")) {
			return 1;
		}
		return 0;
	}

	public final String getSta() {
		String str = ContextHolderUtils.getRequest().getParameter("Sta");

		return str;
	}
	public int getPageIdx() {
		String str = ContextHolderUtils.getRequest().getParameter("PageIdx");
		if (str == null || str.equals("") || str.equals("null"))
			return 1;
		return Integer.parseInt(str);
		// set
		// {
		// ViewState["PageIdx",value;
		// }
	}

	public int getPageSize() {
		String str = ContextHolderUtils.getRequest().getParameter("PageSize");
		if (str == null || str.equals("") || str.equals("null"))
			return 10;
		return Integer.parseInt(str);
		// set
		// {
		// ViewState["PageIdx",value;
		// }
	}

	public final String getKey() {
		return ContextHolderUtils.getRequest().getParameter("Key");
	}

	public final String getActionType()
	{
		String s = ContextHolderUtils.getRequest().getParameter("ActionType");
		if (s == null)
		{
			s = ContextHolderUtils.getRequest().getParameter("DoType");
		}

		return s;
	}

	public final Long getOID()
	{
		String str = this.GetRequestVal("RefOID"); // context.Request.QueryString["RefOID"];
		if (DataType.IsNullOrEmpty(str) == true)
			str = this.GetRequestVal("OID");  //context.Request.QueryString["OID"];

		if (DataType.IsNullOrEmpty(str) == true)
			str="0";

		return Long.parseLong(str);

	}

	public String getEnName()
	{
		return GetRequestVal("EnName");
	}
	public String getRefNo()
	{
		return GetRequestVal("RefNo");
	}

	public final String getFK_Emp()
	{
		return GetRequestVal("FK_Emp");
	}
	public final String getPageID()
	{
		return GetRequestVal("PageID");
	}

	public boolean getIsCC()
	{
		String s = ContextHolderUtils.getRequest().getParameter("Paras");
		if (s == null)
		{
			return false;
		}

		if (s.contains("IsCC"))
		{
			return true;
		}
		return false;
	}

	public final int getallRowCount() {
		int i = 0;
		try {
			i = Integer.parseInt(ContextHolderUtils.getRequest().getParameter(
					"rowCount"));
		} catch (Exception e) {
			return 0;
		}
		return i;
	}
	public final String getTB_Doc()
	{
		return GetRequestVal("TB_Doc");
	}

	public String getSID()
	{
		return GetRequestVal("SID");
	}

	//public String FK_Node;
	//public String FID;
	//public String WorkID;
	//public String FK_Flow;
	//public String MyPK;

//	public void setMyPK(String myPK) {
//		MyPK = myPK;
//	}

//	@PostConstruct
//	public void init(){
//		HttpServletRequest request = ContextHolderUtils.getRequest();
//		//setFK_Node(request.getParameter("FK_Node"));
//		//setFID(request.getParameter("FID"));
//		//setWorkID(request.getParameter("WorkID"));
//		//setFK_Flow(request.getParameter("FK_Flow"));
//		//setMyPK(request.getParameter("MyPK"));
//	}

	/**
	 * 输出Alert
	 * @param response
	 * @param msg
	 * @throws IOException
	 */
	protected void printAlert(HttpServletResponse response, String msg) throws IOException{
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		out.write("<script language='javascript'>alert('" + msg + "');</script>");
		out.flush();
	}
	protected void printAlertReload(HttpServletResponse response, String msg,String url) throws IOException{
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		out.write("<script language='javascript'>alert('" + msg + "');window.location.href='"+url+"';</script>");
		out.flush();
	}
	protected void windowReload(HttpServletResponse response, String url) throws IOException{
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		out.write("<script language='javascript'>window.location.href='"+url+"';</script>");
		out.flush();
	}
	protected void wirteMsg(HttpServletResponse response, String msg) throws IOException{
		if(null == msg){
			return;
		}
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		out.write(msg);
		out.flush();
	}
	protected void winCloseWithMsg(HttpServletResponse response, String mess) throws IOException
	{
		//this.ResponseWriteRedMsg(mess);
		//return;
		mess = mess.replace("'", "＇");

		mess = mess.replace("\"", "＂");

		mess = mess.replace(";", "；");
		mess = mess.replace(")", "）");
		mess = mess.replace("(", "（");

		mess = mess.replace(",", "，");
		mess = mess.replace(":", "：");


		mess = mess.replace("<", "［");
		mess = mess.replace(">", "］");

		mess = mess.replace("[", "［");
		mess = mess.replace("]", "］");


		mess = mess.replace("@", "\\n@");

		mess = mess.replace("\r\n", "");

		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		out.write("<script language='javascript'>alert('" + mess + "'); window.close()</script>");
		out.flush();
	}

	protected void winCloseWithMsg1(HttpServletResponse response,String val) throws IOException{
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		out.write("<script language='javascript'> if(window.opener != undefined){window.top.returnValue = '" + val + "';} else { window.returnValue = '" + val + "';} window.close(); </script>");
		out.flush();
	}

	protected void winClose(HttpServletResponse response) throws IOException{
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		out.write("<script language='javascript'> window.close();</script>");
		out.flush();
	}

//	public void setFK_Node(String fK_Node) {
//		FK_Node = fK_Node;
//	}
}
