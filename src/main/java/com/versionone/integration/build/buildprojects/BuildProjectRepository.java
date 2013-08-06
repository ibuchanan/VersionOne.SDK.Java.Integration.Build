package com.versionone.integration.build.buildprojects;

import com.versionone.DB.DateTime;
import com.versionone.apiclient.Query;

public interface BuildProjectRepository {
	boolean isDirty() throws BuildProjectRepositoryException;

	void reload() throws BuildProjectRepositoryException;

	Query buildQueryForAllBuildProjects();

	boolean areThereNewBuildProjectsAfter(DateTime thisDate)
			throws BuildProjectRepositoryException;

	String findOidByName(String name) 
			throws BuildProjectRepositoryException;
}
