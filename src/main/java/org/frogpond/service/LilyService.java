package org.frogpond.service;

import org.apache.solr.client.solrj.SolrQuery;
import org.frogpond.LilyException;
import org.frogpond.SearchException;
import org.lilyproject.repository.api.*;
import java.util.List;
import java.util.Map;

public interface LilyService {

    /**
     * Perform the given solrQuery.
     *
     *  The SolrResult will be parsed and the results will be converted to beans.
     *
     * @param query         the query to invoke
     * @param resultType    the type of items to return
     * @param <T>           the type of items to return
     * @return  A list of beans which resulted in invoking the search query.
     * @throws SearchException  if searching failed.
     */
    <T> List<T> search(SolrQuery query, Class<T> resultType) throws SearchException;

    /**
     * Enrich the given object with data from the given links.
     * <p/>
     * Enriching an object means we will retrieve additional data not retrieved by default. The links parameter
     * which can be provided with this method contains the property names to enrich. If the property is a Lily
     * Link Primitive, it will be resolved and the result stored within the object.
     *
     * @param pojo  the object to enrich
     * @param links an array of link property names which should be fetched
     * @throws LilyException if any kind of exception occurs. It will be converted to a LilyException
     */
    void resolve(Object pojo, String... links) throws LilyException;
    void resolve(RecordId recordId, Object pojo, String... links) throws LilyException;

    /**
     * Get the record with the given id and of the given type.
     *
     * @param <T>        the record type
     * @param id         the id of the record to retrieve
     * @param resultType the type of the record to retrieve
     * @throws LilyException if any kind of exception occurs. It will be converted to a LilyException
     * @return the record matching the given id and type, or null if no such
     * record could be found.
     */
    <T> T get(Class<T> resultType, String id) throws LilyException;

    /**
     * Get the record with the given id and of the given type.
     *
     * @param <T>        the record type
     * @param id         the id of the record to retrieve
     * @param resultType the type of the record to retrieve
     * @param version    the version of the record to retrieve
     * @throws LilyException if any kind of exception occurs. It will be converted to a LilyException
     * @return the record matching the given id and type, or null if no such
     * record could be found.
     */
    <T> T get(Class<T> resultType, String id, Long version) throws LilyException;

    /**
     * Get the record with the given id and of the given type.
     *
     * @param <T>            the record type
     * @param id             the id of the record to retrieve
     * @param recordVariants the variants of the record to retrieve
     * @param resultType     the type of the record to retrieve
     * @throws LilyException if any kind of exception occurs. It will be converted to a LilyException
     * @return the record matching the given id and type, or null if no such
     * record could be found.
     */
    <T> T get(Class<T> resultType, String id, Map<String, String> recordVariants) throws LilyException;

    /**
     * Get the record with the given id and of the given type.
     *
     * @param <T>            the record type
     * @param id             the id of the record to retrieve
     * @param recordVariants the variants of the record to retrieve
     * @param resultType     the type of the record to retrieve
     * @param version        the version of the record to retrieve
     * @throws LilyException if any kind of exception occurs. It will be converted to a LilyException
     * @return the record matching the given id and type, or null if no such
     * record could be found.
     */
    <T> T get(Class<T> resultType, String id, Map<String, String> recordVariants, Long version) throws LilyException;

    /**
     * Check if the given pojo already exists inside lily.
     *
     * @param pojo the pojo to check
     * @return true if it already exists, false if not
     * @throws LilyException if any kind of exception occurs. It will be converted to a LilyException
     */
    boolean exists(Object pojo) throws LilyException;

    /**
     * Store the given object.
     * <p/>
     * The recordId and variants are retrieved from the given object.
     * A new record will be created if none already existed with the given recordId. If one could be found, the
     * record will be updated.
     *
     * @param pojo the object to store
     * @throws LilyException if any kind of exception occurs. It will be converted to a LilyException
     */
    void store(Object pojo) throws LilyException;

    /**
     * Create the given pojo.
     * <p/>
     * The recordId and variants are retrieved from the given object.
     *
     * @param pojo the pojo to create
     * @throws LilyException if any kind of exception occurs. It will be converted to a LilyException
     */
    void create(Object pojo) throws LilyException;

    /**
     * Update the given pojo.
     *
     * @param pojo the pojo to update
     * @throws LilyException if any kind of exception occurs. It will be converted to a LilyException
     */
    void update(Object pojo) throws LilyException;

    /**
     * Delete the given pojo.
     *
     * @param pojo the pojo to delete
     * @throws LilyException if any kind of exception occurs. It will be converted to a LilyException
     */
    void delete(Object pojo) throws LilyException;
}
