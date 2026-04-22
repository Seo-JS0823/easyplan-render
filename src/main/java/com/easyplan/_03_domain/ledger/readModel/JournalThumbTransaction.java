package com.easyplan._03_domain.ledger.readModel;

import java.time.LocalDate;
import java.util.List;

import com.easyplan._03_domain.ledger.model.account.AccountType;
import com.easyplan._03_domain.ledger.model.journal.EntryType;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JournalThumbTransaction {
    private Long journalId;
    private LocalDate transactionDate;
    private Long totalAmount;
    private String memo;
    private AccountSideInfo debit;
    private AccountSideInfo credit;
    
    @QueryProjection
    public JournalThumbTransaction(Long journalId, LocalDate transactionDate, Long totalAmount, String memo, List<AccountSideInfo> sideInfos) {
        this.journalId = journalId;
        this.transactionDate = transactionDate;
        this.totalAmount = totalAmount;
        this.memo = memo;
        
        // 리스트로 들어온 데이터를 타입에 따라 할당
        if (sideInfos != null) {
            for (AccountSideInfo info : sideInfos) {
                if (info.getEntryType() == EntryType.DEBIT) {
                    this.debit = info;
                } else if (info.getEntryType() == EntryType.CREDIT) {
                    this.credit = info;
                }
            }
        }
    }

    @Getter
    public static class AccountSideInfo {
        private String name;
        private AccountType type;
        private EntryType entryType;

        @QueryProjection
        public AccountSideInfo(String name, AccountType type, EntryType entryType) {
            this.name = name;
            this.type = type;
            this.entryType = entryType;
        }
    }
}