package com.drs.cyberpunk.listeners;

public interface WorldRenderListener {
	public void changeMapEvent(String mapName);
	
	public void updateDamagePlayerHealthUI(int damage);
	public void updateHealPlayerHealthUI(int heal);
	public void updatePlayerMaxHealth(int maxHealth);
	
	public void updateIncreaseCyberspaceAlertUI(int increase);
	public void updateDecreaseCyberspaceAlertUI(int decrease);
	
	public void startShakingCamera();
	
	public void alertTriggered();
	public void alertStopped();
	
	public void addMessageToTerminal(String message);
}
