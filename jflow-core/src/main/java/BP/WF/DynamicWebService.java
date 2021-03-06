package BP.WF;

/** 
 调用webservices.
*/
public class DynamicWebService
{
	/** 
	 调用webservices.
	*/
	private DynamicWebService()
	{

	}
	/** 
	 动态调用web服务
	 
	 @param url 链接串
	 @param methodname 方法名
	 @param args 参数
	 @return 
	*/
	public static Object InvokeWebService(String url, String methodName,
			Object[] args)
	{
		return args;
		// return DynamicWebService.InvokeWebService(url, null, methodName,
		// args);
	}
	/** 
	 动态调用web服务
	 @param url
	 @param classname
	 @param methodname
	 @param args
	 @return 
	*/
	/*
	public static Object InvokeWebService(String url, String className, String methodName, Object[] args)
	{
		String namespace = "EnterpriseServerBase.WebService.DynamicWebCalling";
		if ((className == null) || (className.equals("")))
		{
			className = DynamicWebService.GetWsClassName(url);
		}
		try
		{
			//获取WSDL 
			WebClient wc = new WebClient();
			Stream stream = wc.OpenRead(url + "?WSDL");
			ServiceDescription sd = ServiceDescription.Read(stream);
			ServiceDescriptionImporter sdi = new ServiceDescriptionImporter();
			sdi.AddServiceDescription(sd, "", "");
			CodeNamespace cn = new CodeNamespace(namespace);

			//生成客户端代理类代码 
			CodeCompileUnit ccu = new CodeCompileUnit();
			ccu.Namespaces.Add(cn);
			sdi.Import(cn, ccu);
			CodeDomProvider icc = CodeDomProvider.CreateProvider("CSharp");
			//CSharpCodeProvider csc = new CSharpCodeProvider();
			//ICodeCompiler icc = csc.CreateCompiler();

			//设定编译参数 
			CompilerParameters cplist = new CompilerParameters();
			cplist.GenerateExecutable = false;
			cplist.GenerateInMemory = true;
			cplist.ReferencedAssemblies.Add("System.dll");
			cplist.ReferencedAssemblies.Add("System.XML.dll");
			cplist.ReferencedAssemblies.Add("System.Web.Services.dll");
			cplist.ReferencedAssemblies.Add("System.Data.dll");
			//编译代理类 
			CompilerResults cr = icc.CompileAssemblyFromDom(cplist, ccu);
			if (true == cr.Errors.HasErrors)
			{
				StringBuilder sb = new StringBuilder();
				for (System.CodeDom.Compiler.CompilerError ce : cr.Errors)
				{
					sb.append(ce.toString());
					sb.append(System.Environment.NewLine);
				}
				throw new RuntimeException(sb.toString());
			}

			//生成代理实例，并调用方法 
			System.Reflection.Assembly assembly = cr.CompiledAssembly;
			java.lang.Class t = assembly.GetType(namespace + "." + className, true, true);
			Object obj = Activator.CreateInstance(t);

			//设置CookieContainer 1987raymond添加            
			PropertyInfo property = t.GetProperty("CookieContainer");
			property.SetValue(obj, container, null);

			java.lang.reflect.Method mi = t.getMethod(methodName);
			return mi.invoke(obj, args);
		}
		catch (RuntimeException ex)
		{
			throw ex;
		}
	}
	*/
	private static String GetWsClassName(String wsUrl)
	{
		String[] parts = wsUrl.split("[/]", -1);
		String[] pps = parts[parts.length - 1].split("[.]", -1);
		return pps[0];
	}
}