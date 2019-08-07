package com.biz.bbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.biz.bbs.mapper.BBsDao;
import com.biz.bbs.model.BBsDto;
import com.biz.bbs.model.BBsVO;
import com.biz.bbs.model.FileVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BBsService {

	@Autowired
	BBsDao bDao;
	
	@Autowired
	FileService fileService;
	
	public List<BBsVO> bbsList(){
		List<BBsVO> bbsList = bDao.selectAll();
		
		return bbsList;
	}

	public List<BBsDto> bbsListForFile(){
		List<BBsDto> bbsList = bDao.selectAllForFile();
		
		return bbsList;
	}
	/*
	 *글쓰기를 수행할 떄 file을 같이 upload를 하면
	 *해당하는 글의 bbs_seq를 조회하여 
	 *file table에 저장할 떄 같이 저장을 수행해줘야 한다.
	 *
	 * 	1. 게시글 저장
	 * 	2. 게시글의 bbs_seq를 조회
	 * 	3. 파일정보를 insert할때 해당하는 bbs_seq를 같이 저장
	 * 	4. 게시글을 조회할 때 해당하는 파일들을 같이 조회할 수 있다
	 */
	public int insert(BBsVO bbsVO) {

		// 1. 게시글을 저장
		//	저장후에는 bbsVO.bbs_seq에 새로 생성된 PK값이 담겨있다.
		int ret = bDao.insert(bbsVO);
		
		//파일의 개수가 0이상이고 
		//0번파일의 이름이 있을 경우에만 실행 
		if(bbsVO.getBbs_files().size() > 0 &&
				!bbsVO.getBbs_files().get(0)
				.getOriginalFilename().isEmpty() ) {
		
			// 2. 게시글의 bbs_seq가 추가되고
			// 3. UUID 파일이름이 추가된 fileList를 생성하고
			List<FileVO> fileList = fileService.getFileList(bbsVO);
			
			// 4. 그 리스트를 모두 insert 수행
			fileService.fileListInsert(fileList);
			
			//long bbs_seq = bbsVO.getBbs_seq();
			//fileInsert(bbs_seq)
			//log.debug("게시판 SEQ : "+bbsVO.getBbs_seq());
		}
		

		return ret;
	}

	public BBsDto getContent(long bbs_seq) {
		
		BBsDto bbsDto = bDao.findBySeqForFile(bbs_seq);
		
		return bbsDto;
	}

	public int update(BBsVO bbsVO) {

		int ret = bDao.update(bbsVO);
		
		return 0;
	}
	
}
