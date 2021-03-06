package edu.rpi.tw.escience.semanteco;

import java.net.URI;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.query.Query;

/**
 * The ModuleManager provides a mechanism for collecting, configuring, and
 * processing modules in the SemantEco framework.
 * 
 * @author ewpatton
 *
 */
public interface ModuleManager {
	/**
	 * Gets a module given its processed name. The processed name
	 * is the name of the module with whitespace replaced with dashes,
	 * e.g. "Data-Source" instead of "Data Source"
	 * @param name
	 * @return
	 */
	Module getModuleByName(String name);
	
	/**
	 * Requests that the ModuleManager help construct the UI by having
	 * each of its modules visit the provided SemantEcoUI object.
	 * @param ui
	 * @param params
	 */
	void buildUserInterface(SemantEcoUI ui, Request request);
	
	/**
	 * Requests that the ModuleManager help construct the ontology model
	 * by having each of its modules visit the provided OntModel object.
	 * @param model
	 * @param params
	 */
	void buildOntologyModel(OntModel model, Request request);
	
	/**
	 * Requests that the ModuleManager help construct the data model
	 * by having each of its modules visit the provided Model object.
	 * @param model
	 * @param params
	 */
	void buildDataModel(Model model, Request request);
	
	/**
	 * Requests that the provided module give an updated code fragment
	 * for its facet due to parameter changes on the client.
	 * @param module
	 * @param params
	 * @return
	 */
	String updateFragmentForFacet(Module module, Request request);
	
	/**
	 * Allows any modules in this ModuleManager interface to change the provided
	 * query prior to execution. Optional parameters passed from the client are
	 * provided.
	 * @param query
	 * @param params
	 */
	void augmentQuery(Query query, Request request);
	
	/**
	 * Allows any modules in this ModuleManager interface to change the provided
	 * query prior to execution. Optional parameters passed from the client are
	 * provided. If this query was originated from a module, that is passed as
	 * well so that the ModuleManager can ignore it while executing.
	 * @param query
	 * @param params
	 * @param originator
	 */
	void augmentQuery(Query query, Request request, Module originator);
	
	/**
	 * Lists all of the modules managed by this ModuleManager.
	 * @return
	 */
	List<Module> listModules();
	
	/**
	 * Returns the last modified time in milliseconds of the manager. This is
	 * primarily useful for when modules are redeployed in SemantEco so that
	 * other subsystems that rely on being fresh relative to the modules can
	 * perform additional updates (e.g. SemantEcoUI)
	 * @return
	 */
	long getLastModified();
	
	/**
	 * Gets a Domain object named by the URI. One will be created if none exists.
	 * @param uri A URI naming a Domain, by convention it SHOULD be an OWL file. 
	 * @return Domain object named by the URI.
	 */
	Domain getDomain(URI uri);

	/**
	 * Registers a domain with the manager. Modules should not need to call
	 * this but instead rely on {@link #getDomain(URI)}
	 * @param domain
	 */
	void registerDomain(Domain domain);

	/**
	 * Lists all of the domains known by the manager.
	 * @return
	 */
	List<Domain> listDomains();
}
