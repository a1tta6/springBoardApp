package org.isNotNull.springBoardApp.opportunity.service;

import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.common.exception.AlreadyExistsException;
import org.isNotNull.springBoardApp.common.exception.MissingFieldException;
import org.isNotNull.springBoardApp.opportunity.adapter.OpportunityEventAdapter;
import org.isNotNull.springBoardApp.opportunity.dto.OpportunityFileDTO;
import org.isNotNull.springBoardApp.opportunity.exception.OpportunityFileNotFoundException;
import org.isNotNull.springBoardApp.opportunity.exception.OpportunityNotFoundException;
import org.isNotNull.springBoardApp.opportunity.repository.OpportunityFileRepository;
import org.isNotNull.springBoardApp.tables.daos.OpportunityDao;
import org.isNotNull.springBoardApp.tables.daos.OpportunityFileDao;
import org.isNotNull.springBoardApp.tables.pojos.OpportunityFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@AllArgsConstructor
public class OpportunityFileService {

    private final OpportunityEventAdapter opportunityEventAdapter;
    private final OpportunityDao opportunityDao;
    private final OpportunityFileDao opportunityFileDao;
    private final OpportunityFileRepository opportunityFileRepository;

    @Transactional
    public Long createOpportunityFile(Long opportunityId, OpportunityFileDTO opportunityFileDTO) {
        opportunityDao.findOptionalById(opportunityId).orElseThrow(() -> new OpportunityNotFoundException(opportunityId));

        opportunityFileDTO.setOpportunityId(opportunityId);

        if (Objects.isNull(opportunityFileDTO.getFileName())) {
            throw new MissingFieldException("fileName");
        }
        if (Objects.isNull(opportunityFileDTO.getFileSize())) {
            throw new MissingFieldException("fileSize");
        }
        if (Objects.isNull(opportunityFileDTO.getFileType())) {
            throw new MissingFieldException("fileType");
        }

        OpportunityFile opportunityFile = opportunityEventAdapter.toOpportunityFile(opportunityFileDTO);

        if (opportunityFileRepository.exists(
                opportunityFileDTO.getFileName(),
                opportunityFileDTO.getFileType(),
                opportunityId
        )) {
            throw new AlreadyExistsException(String.format(
                    "file with name %s and type %s",
                    opportunityFileDTO.getFileName(),
                    opportunityFileDTO.getFileType()
            ));
        }

        opportunityFileDao.insert(opportunityFile);
        return opportunityFile.getFileId();
    }

    @Transactional
    public Long deleteOpportunityFile(Long opportunityId, OpportunityFileDTO opportunityFileDTO) {
        opportunityDao.findOptionalById(opportunityId).orElseThrow(() -> new OpportunityNotFoundException(opportunityId));
        Long fileId = opportunityFileDTO.getFileId();
        if (opportunityFileDao.fetchByFileId(fileId).isEmpty()) {
            throw new OpportunityFileNotFoundException(fileId);
        }
        opportunityFileDao.deleteById(fileId);
        return fileId;
    }

    public ResponseEntity<byte[]> downloadOpportunityFile(Long fileId) {
        OpportunityFile opportunityFile = opportunityFileRepository.fetchById(fileId);

        if (opportunityFile == null) {
            throw new OpportunityFileNotFoundException(fileId);
        }

        OpportunityFileDTO fileDTO = opportunityEventAdapter.toOpportunityFileDto(opportunityFile);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileDTO.getFileType()));
        headers.setContentDispositionFormData("attachment", fileDTO.getFileName());
        headers.setContentLength(fileDTO.getFileSize());

        return new ResponseEntity<>(fileDTO.getFileContent(), headers, HttpStatus.OK);
    }
}
