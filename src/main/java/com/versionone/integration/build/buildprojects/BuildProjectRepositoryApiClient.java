package com.versionone.integration.build.buildprojects;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.versionone.DB.DateTime;
import com.versionone.apiclient.APIException;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.ConnectionException;
import com.versionone.apiclient.EnvironmentContext;
import com.versionone.apiclient.FilterTerm;
import com.versionone.apiclient.IAssetType;
import com.versionone.apiclient.IAttributeDefinition;
import com.versionone.apiclient.MetaException;
import com.versionone.apiclient.OidException;
import com.versionone.apiclient.Query;
import com.versionone.apiclient.QueryResult;

public class BuildProjectRepositoryApiClient implements BuildProjectRepository {
	private EnvironmentContext cx;
	private DateTime mostRecentChangeDateTime;
	private IAssetType buildProjectType;
	private IAttributeDefinition nameAttribute;
	private IAttributeDefinition changeAttribute;
	private Query queryForAllBuildProjects;
	private Map<String, String> nameToOid;
	private static final DateFormat V1STYLE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	public BuildProjectRepositoryApiClient(EnvironmentContext cx) {
		this.cx = cx;
		mostRecentChangeDateTime = null;
		buildProjectType = cx.getMetaModel().getAssetType("BuildProject");
		nameAttribute = buildProjectType.getAttributeDefinition("Name");
		changeAttribute = buildProjectType.getAttributeDefinition("ChangeDateUTC");
		queryForAllBuildProjects = buildQueryForAllBuildProjects();
	}

	public boolean isDirty() throws BuildProjectRepositoryException {
		if (null == mostRecentChangeDateTime) {
			return true;
		}
		return areThereNewBuildProjectsAfter(mostRecentChangeDateTime);
	}

	public void reload() throws BuildProjectRepositoryException {
		nameToOid = new HashMap<String, String>();
		QueryResult result;
		try {
			result = cx.getServices().retrieve(queryForAllBuildProjects);
		} catch (ConnectionException e) {
			throw new BuildProjectRepositoryException(e);
		} catch (APIException e) {
			throw new BuildProjectRepositoryException(e);
		} catch (OidException e) {
			throw new BuildProjectRepositoryException(e);
		}
		for (Asset asset : result.getAssets()) {
			DateTime changeDateTime = null;
			try {
				nameToOid.put((String) asset.getAttribute(nameAttribute)
						.getValue(), asset.getOid().getToken());
				// Remember the most recent change to VersionOne for checking dirty state
				changeDateTime = new DateTime(asset.getAttribute(changeAttribute).getValue());
			} catch (APIException e) {
				throw new BuildProjectRepositoryException(e);
			} catch (MetaException e) {
				throw new BuildProjectRepositoryException(e);
			}
			if ((null == mostRecentChangeDateTime)
					|| (changeDateTime.compareTo(mostRecentChangeDateTime) > 0)) {
				mostRecentChangeDateTime = changeDateTime;
			}
		}
	}

	public boolean areThereNewBuildProjectsAfter(DateTime thisDate)
			throws BuildProjectRepositoryException {
		Query query = new Query(buildProjectType);
		query.getSelection().add(changeAttribute);
		FilterTerm term = new FilterTerm(changeAttribute);
		term.greater(V1STYLE.format(thisDate.getValue()));
		query.setFilter(term);
		QueryResult result;
		try {
			result = cx.getServices().retrieve(query);
		} catch (ConnectionException e) {
			throw new BuildProjectRepositoryException(e);
		} catch (APIException e) {
			throw new BuildProjectRepositoryException(e);
		} catch (OidException e) {
			throw new BuildProjectRepositoryException(e);
		}
		return result.getTotalAvaliable() > 0;
	}

	public Query buildQueryForAllBuildProjects() {
		Query query = new Query(buildProjectType);
		query.getSelection().add(nameAttribute);
		query.getSelection().add(changeAttribute);
		return query;
	}

	public String findOidByName(String name)
			throws BuildProjectRepositoryException {
		if (isDirty()) {
			reload();
		}
		return nameToOid.get(name);
	}

}
