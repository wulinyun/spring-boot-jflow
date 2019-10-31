package BP.En;

import java.util.ArrayList;

/**
 * 属性集合
 */
public class AttrFiles extends ArrayList<AttrFile>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AttrFiles()
	{
	}
	
	/**
	 * 增加文件
	 * 
	 * @param fileNo
	 * @param fileName
	 */
	public final void Add(String fileNo, String fileName)
	{
		this.add(new AttrFile(fileNo, fileName));
		/*
		 * warning this.add(new AttrFile(fileNo, fileName));
		 */
	}
}