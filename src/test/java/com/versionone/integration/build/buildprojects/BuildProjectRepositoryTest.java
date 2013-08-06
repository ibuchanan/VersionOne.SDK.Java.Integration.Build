package com.versionone.integration.build.buildprojects;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Map.Entry;

import com.versionone.apiclient.APIException;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.Attribute;
import com.versionone.apiclient.ConnectionException;
import com.versionone.apiclient.EnvironmentContext;
import com.versionone.apiclient.IAssetType;
import com.versionone.apiclient.IAttributeDefinition;
import com.versionone.apiclient.OidException;
import com.versionone.apiclient.Query;
import com.versionone.apiclient.QueryResult;

public class BuildProjectRepositoryTest {

	@Test
	public void new_build_project_repository_is_dirty() {
		// Given a connection to a VersionOne instance defined in the APIConfiguration.properties
		EnvironmentContext cx = null;
		try {
			cx = new EnvironmentContext();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		// When I create a new repository with that connection
		BuildProjectRepository repository = new BuildProjectRepositoryApiClient(cx);
		// Then it is initially dirty
		boolean dirty = false;
		try {
			dirty = repository.isDirty();
		} catch (BuildProjectRepositoryException e) {
			fail(e.getMessage());
		}
		assertTrue(dirty);
	}

	@Test
	public void query_for_build_project_is_scoped_to_BuildProject() {
		// Given a connection to a VersionOne instance defined in the APIConfiguration.properties
		EnvironmentContext cx = null;
		try {
			cx = new EnvironmentContext();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		// And a new repository with that connection
		BuildProjectRepository repository = new BuildProjectRepositoryApiClient(cx);
		// When I build the query for story statuses
		Query query = repository.buildQueryForAllBuildProjects();
		// Then the asset type is BuildProject
		assertEquals("BuildProject", query.getAssetType().getToken());
	}

	@Test
	public void query_for_request_categories_selects_name() {
		// Given a connection to a VersionOne instance defined in the APIConfiguration.properties
		EnvironmentContext cx = null;
		try {
			cx = new EnvironmentContext();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		// And a new repository with that connection
		BuildProjectRepository repository = new BuildProjectRepositoryApiClient(cx);
		// And a reference to the BuildProject asset type
		IAssetType assetType = cx.getMetaModel().getAssetType("BuildProject");
		// And a reference to the name attribute
		IAttributeDefinition nameAttribute = assetType.getAttributeDefinition("Name");
		// When I build the query for build projects
		Query query = repository.buildQueryForAllBuildProjects();
		// Then the query selects the name attribute
		assertTrue(query.getSelection().contains(nameAttribute));
	}

	@Test
	public void query_for_build_projects_selects_change_date() {
		// Given a connection to a VersionOne instance defined in the APIConfiguration.properties
		EnvironmentContext cx = null;
		try {
			cx = new EnvironmentContext();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		// And a new repository with that connection
		BuildProjectRepository repository = new BuildProjectRepositoryApiClient(cx);
		// And a reference to the build project asset type
		IAssetType assetType = cx.getMetaModel().getAssetType("BuildProject");
		// And a reference to the name attribute
		IAttributeDefinition nameAttribute = assetType.getAttributeDefinition("ChangeDateUTC");
		// When I build the query for request categories
		Query query = repository.buildQueryForAllBuildProjects();
		// Then the query selects the name attribute
		assertTrue(query.getSelection().contains(nameAttribute));
	}

	@Test
	public void reload_is_clean() {
		// Given a connection to a VersionOne instance defined in the APIConfiguration.properties
		EnvironmentContext cx = null;
		try {
			cx = new EnvironmentContext();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		// And a new repository with that connection
		BuildProjectRepository repository = new BuildProjectRepositoryApiClient(cx);

		QueryResult result = null;
		try {
			result = cx.getServices().retrieve(repository.buildQueryForAllBuildProjects());
		} catch (ConnectionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (APIException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (OidException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (Asset asset : result.getAssets()) {
			for (Entry<String, Attribute> attribute : asset.getAttributes().entrySet()) {
				try {
					String k = attribute.getKey();
					Object v = attribute.getValue().getValue();
				} catch (APIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


		// When I reload the repository
		try {
			repository.reload();
		} catch (BuildProjectRepositoryException e) {
			fail(e.getMessage());
		}
		// Then the repository is not dirty
		boolean dirty = false;
		try {
			dirty = repository.isDirty();
		} catch (BuildProjectRepositoryException e) {
			fail(e.getMessage());
		}
		assertFalse(dirty);
	}
}
