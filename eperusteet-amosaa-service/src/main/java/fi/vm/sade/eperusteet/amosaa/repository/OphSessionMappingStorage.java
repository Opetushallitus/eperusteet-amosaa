package fi.vm.sade.eperusteet.amosaa.repository;

import org.jasig.cas.client.session.SessionMappingStorage;

public interface OphSessionMappingStorage extends SessionMappingStorage {

  void clean();
}