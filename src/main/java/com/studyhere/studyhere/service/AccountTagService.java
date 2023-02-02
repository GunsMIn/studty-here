package com.studyhere.studyhere.service;

import com.studyhere.studyhere.repository.AccountTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountTagService {

    private final AccountTagRepository accountTagRepository;



}
