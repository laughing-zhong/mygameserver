package game.web.controller;

import game.app.content.Content;
import game.app.content.service.ContentService;
import game.app.helper.AuthorizationHelper;

import java.io.ByteArrayInputStream;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/content")
public class ContentController {

	private static final Logger LOGGER = LoggerFactory.getLogger( ContentController.class );
	private static final String GAME_DATA_ENCODING = "UTF-8";

	private JAXBContext jaxbContext;

	@Value("${content.port.restriction}")
	private String contentPortRestriction = "8080";
	
	@Inject
	private AuthorizationHelper authorizationHelper;

	@Inject
	private ContentService contentService;
	

	@PostConstruct
	public void setUp(){
		try {
			jaxbContext = JAXBContext.newInstance( Content.class );
		} catch ( JAXBException e ) {
			LOGGER.error("Unable to create JAXBContext: {}", e);
		}
	}


	
//	@RequestMapping(value = "/activation", method = RequestMethod.POST)
//	@ResponseBody
//	public String publishActicationCode(@RequestBody byte[] contentData, HttpServletRequest request) {
//		authorizationHelper.isAuthorizedPort( contentPortRestriction, request.getLocalPort() );	
//		ActivitionCode  activitionCode = unmarshalActivication(contentData);
//		 contentService.publishActivitionContent(activitionCode);
//		 return "publish sucess";
//	}
	

//	@RequestMapping(value="/del_activation", method = RequestMethod.GET)
//	@ResponseBody
//	public void delActivation(
//			@RequestParam(value = "batchId", required = true) String batchId,
//			@RequestParam(value = "typeId", required = true) String typeId)
//	{
//		activitionCodeService.delActivition(batchId, typeId);
//	}
	
	@SuppressWarnings("SpellCheckingInspection")
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public void publishContent( @RequestBody byte[] contentData, HttpServletRequest request,
								@RequestParam(value = "allowremoves", required = false) Boolean allowRemoves,
								@RequestParam(value = "revision", required = false) String revision,
								@RequestParam(value = "silent", required = false) Boolean silent) {

		authorizationHelper.isAuthorizedPort( contentPortRestriction, request.getLocalPort() );

		LOGGER.info( "publishContent()" );

		Content content = unmarshalContent( contentData );

		if ( content != null ) {
			//contentService.publishContent( content, contentData, revision, Boolean.TRUE.equals( allowRemoves ), Boolean.TRUE.equals( silent ) );
			/*
			 * 为了解决couchbase view使用的时候document尺寸过大导致无法查询的异常
			 * 局域网服务器特别处理,不保存contentData
			 */
			contentService.publishContent( content, null, revision, Boolean.TRUE.equals( allowRemoves ), Boolean.TRUE.equals( silent ) );
		} else {
			throw new IllegalArgumentException( "Could not unmarshal game data content." );
		}

		LOGGER.info( "publishContent() exit" );
	}
	

	

//	@RequestMapping(value = "/current", method = RequestMethod.GET, produces="application/xml")
//	@ResponseBody
//	public String getCurrentContent( HttpServletRequest request ) {
//
//		authorizationHelper.isAuthorizedPort( contentPortRestriction, request.getLocalPort() );
//
//		ContentDO contentDO = contentService.getContent();
//
//		if (contentDO == null)
//			throw new RecordNotFoundException( "Content not found" );
//
//		StringBuilder stringBuilder = new StringBuilder();
//		stringBuilder.append( String.format("<!-- revision: %s -->\n", contentDO.getRevision() ) );
//
//		try {
//			stringBuilder.append( new String( contentDO.getContentData(), GAME_DATA_ENCODING ) );
//		} catch ( UnsupportedEncodingException e ) {
//			LOGGER.warn("Could not decode contentData to string: {}", e);
//		}
//
//		return stringBuilder.toString();
//	}


	@SuppressWarnings("SpellCheckingInspection")
	private Content unmarshalContent( byte[] content ) {
		Content result = null;
		try {
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			result = (Content)unmarshaller.unmarshal( new ByteArrayInputStream( content ) );
		} catch ( JAXBException e ) {
			LOGGER.error("unmarshalContent() exception: {}", e);
			throw new IllegalArgumentException(e);
		}
		return result;
	}
	
	
//	private ActivitionCode  unmarshalActivication(byte[] content){
//		ActivitionCode  result = null;
//		try {
//			JAXBContext jaxbContext = JAXBContext.newInstance( ActivitionCode.class );
//			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//			result = (ActivitionCode)unmarshaller.unmarshal( new ByteArrayInputStream( content ) );
//		} catch ( JAXBException e ) {
//			LOGGER.error("unmarshalContent() exception: {}", e);
//			throw new IllegalArgumentException(e);
//		}
//		return result;
//		
//	}

	


}
