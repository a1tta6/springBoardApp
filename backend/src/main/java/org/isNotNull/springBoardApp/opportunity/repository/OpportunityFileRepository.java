package org.isNotNull.springBoardApp.opportunity.repository;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.isNotNull.springBoardApp.tables.pojos.OpportunityFile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.isNotNull.springBoardApp.tables.OpportunityFile.OPPORTUNITY_FILE;

@Repository
@AllArgsConstructor
public class OpportunityFileRepository {
    private final DSLContext dslContext;

    public boolean exists(String fileName, String fileType, Long opportunityId) {
        Long count = Optional.ofNullable(
                dslContext
                        .selectCount()
                        .from(OPPORTUNITY_FILE)
                        .where(OPPORTUNITY_FILE.FILE_NAME.eq(fileName))
                        .and(OPPORTUNITY_FILE.FILE_TYPE.eq(fileType))
                        .and(OPPORTUNITY_FILE.OPPORTUNITY_ID.eq(opportunityId))
                        .fetchOneInto(Long.class)
        ).orElse(0L);

        return count > 0L;
    }

    public OpportunityFile fetchById(Long id) {
        return dslContext
                .selectFrom(OPPORTUNITY_FILE)
                .where(OPPORTUNITY_FILE.FILE_ID.eq(id))
                .fetchOneInto(OpportunityFile.class);
    }
}
