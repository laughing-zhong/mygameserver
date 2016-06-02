package com.ares.app.content;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.validation.Valid;

import com.ares.app.content.vip.VipConfig;



/**
 * Root container for all artifacts that can be published to the game.
 *
 * @author m.mcbride
 *
 */

@XmlAccessorType( XmlAccessType.FIELD )
@XmlRootElement(name = "content")
public class Content
{


	
    
	@Valid
    @XmlElement(name="vip")
    private VipConfig  vipConfig;
    

	public VipConfig getVipConfig() {
		return vipConfig;
	}



	public Content(){}




	
}
