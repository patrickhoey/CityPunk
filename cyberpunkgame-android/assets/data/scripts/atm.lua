--This script represents an entity
require("common_includes")

function create(entity)
 	-- import class into Lua 
  	local ActionsEnum = java.require("com.drs.cyberpunk.enums.ActionsEnum")

	--Initialize fields through public methods 
	entity:setEntityImagePath(entity.atmBitmap)
	entity:setIsActionMenuEnabled(true)
	entity:setExistsOffScreen(true)
	entity:setIsHackable(true)
	entity:loadWalkingAnimation(1,1,1)
	entity:addActionItemToBeginning(ActionsEnum.EXIT:toString())
	entity:addActionItemToBeginning(ActionsEnum.ANALYZE:toString())
	entity:addActionItemToBeginning(ActionsEnum.JACK_IN:toString())
	
	entity:setSecurityLevel(entity.ENUM_SECURITYLEVEL_GREENHOST)

end


function runActionItem(entity,action)

    local ActionsEnum = java.require("com.drs.cyberpunk.enums.ActionsEnum")
    local MapLocationEnum = java.require("com.drs.cyberpunk.enums.MapLocationEnum")
	
	if action:compareTo(ActionsEnum.EXIT) == 0 then
		--print("EXIT")
		return
	elseif action:compareTo(ActionsEnum.ANALYZE) == 0 then
		--print("ANALYZE")
		entity:playAnalysisSound()
		entity:getWorldRenderListener():addMessageToTerminal(entity._TERMINAL_MESSAGE .. entity:getSecurityLevel():toString())
		return
	elseif action:compareTo(ActionsEnum.JACK_IN) == 0 then
		entity:worldRenderMapChange(MapLocationEnum.SUBGRID001:toString())
		--print("JACK IN")
		return	
	end
		 
end

