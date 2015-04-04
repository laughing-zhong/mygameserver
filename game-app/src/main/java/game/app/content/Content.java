package game.app.content;

import game.app.content.vip.VipConfig;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.validation.Valid;



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
