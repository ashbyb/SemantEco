package edu.rpi.tw.escience.semanteco.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import edu.rpi.tw.escience.semanteco.HierarchicalMethod;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.QueryMethod.HTTP;

/**
 * JavaScriptGenerator provides some basic utilities for creating
 * JavaScript used by the client to interact with the server.
 * @author ewpatton
 *
 */
public final class JavaScriptGenerator {

	private JavaScriptGenerator() {
	
	}
	
	/**
	 * Generates JavaScript to provide an AJAX interface for talking
	 * to the specified Module. Any method annotated with \@QueryMethod
	 * will be provided an implementation on the client.
	 * @param mod
	 * @return
	 */
	public static String ajaxForModule(Module mod) {
		final Class<?> cls = mod.getClass();
		final StringBuilder result = new StringBuilder("var "+cls.getSimpleName()+" = {");
		if(SemantEcoConfiguration.get().isDebug()) {
			result.append("\n");
		}
		int methodCount = 0;
		
		final Method[] methods = cls.getMethods();
		final Collection<Method> hms = new ArrayList<Method>();
		boolean first = true;
		for(int i=0;i<methods.length;i++) {
			Method m = methods[i];
			if(m.isAnnotationPresent(QueryMethod.class) ||
				m.isAnnotationPresent(HierarchicalMethod.class)) {
				methodCount++;
				if(first) {
					first = false;
				}
				else {
					result.append(",");
					if(SemantEcoConfiguration.get().isDebug()) {
						result.append("\n");
					}
				}
				if(m.isAnnotationPresent(QueryMethod.class)) {
					result.append(processQueryMethod(cls, m));
				}
				else if(m.isAnnotationPresent(HierarchicalMethod.class)) {
					hms.add(m);
					result.append(processHierarchicalMethod(cls, m));
				}
			}
		}
		if(SemantEcoConfiguration.get().isDebug()) {
			result.append("\n");
		}
		result.append("};");
		result.append("$(window).bind(\"initialize\",function(){");
		for(Method m : hms) {
			result.append("SemantEcoUI.HierarchicalFacet.create(\"#");
			result.append(NameUtils.cleanName(cls.getSimpleName()));
			result.append(" div.facet div.hierarchy\", ");
			result.append(cls.getSimpleName());
			result.append(", \"");
			result.append(m.getName());
			result.append("\", \"");
			final HierarchicalMethod hm = m.getAnnotation(HierarchicalMethod.class);
			result.append(hm.parameter());
			result.append("\");");
		}
		result.append("});");
		
		if(methodCount == 0) {
			return "";
		}
		else {
			return result.toString();
		}
	}
	
	private static String processQueryMethod(final Class<?> cls, final Method m) {
		final String ending = (SemantEcoConfiguration.get().isDebug() ? "\n  " : "");
		final String ending2 = (SemantEcoConfiguration.get().isDebug() ? "  " : "");
		final StringBuilder result = new StringBuilder();
		final QueryMethod attrs = m.getAnnotation(QueryMethod.class);
		result.append("\"");
		result.append(m.getName());
		result.append("\": ");
		result.append("function(args,success,error){");
		result.append(ending);
		result.append(ending2);
		result.append("var a=$.extend({},SemantEcoUI.getState(),args);");
		result.append(ending);
		result.append(ending2);
		result.append("var b=$.ajax(SemantEco.restBaseUrl+\"");
		result.append(cls.getSimpleName());
		result.append("/");
		result.append(m.getName());
		result.append("\",{\"data\":SemantEco.prepareArgs(a)");
		if(attrs != null) {
			if(attrs.method() == HTTP.POST) {
				result.append(",\"type\":\"POST\"");
			}
		}
		result.append("});");
		result.append(ending);
		result.append(ending2);
		result.append("if(success)");
		result.append(ending);
		result.append(ending2);
		result.append(ending2);
		result.append("b.done(success);");
		result.append(ending);
		result.append(ending2);
		result.append("if(error)");
		result.append(ending);
		result.append(ending2);
		result.append(ending2);
		result.append("b.fail(error);");
		result.append(ending);
		result.append(ending2);
		result.append("}");
		return result.toString();
	}

	private static String processHierarchicalMethod(final Class<?> cls, final Method m) {
		final String ending = (SemantEcoConfiguration.get().isDebug() ? "\n  " : "");
		final String ending2 = (SemantEcoConfiguration.get().isDebug() ? "  " : "");
		final StringBuilder result = new StringBuilder();
		result.append("\"");
		result.append(m.getName());
		result.append("\": ");
		result.append("function(mode,args,success,error){");
		result.append(ending);
		result.append(ending2);
		result.append("var a=$.extend({'mode':mode},SemantEcoUI.getState(),args);");
		result.append(ending);
		result.append(ending2);
		result.append("var b=$.ajax(SemantEco.restBaseUrl+\"");
		result.append(cls.getSimpleName());
		result.append("/");
		result.append(m.getName());
		result.append("\",{\"data\":SemantEco.prepareArgs(a)});");
		result.append(ending);
		result.append(ending2);
		result.append("if(success)");
		result.append(ending);
		result.append(ending2);
		result.append(ending2);
		result.append("b.done(success);");
		result.append(ending);
		result.append(ending2);
		result.append("if(error)");
		result.append(ending);
		result.append(ending2);
		result.append(ending2);
		result.append("b.fail(error);");
		result.append(ending);
		result.append(ending2);
		result.append("}");
		return result.toString();
	}

}
