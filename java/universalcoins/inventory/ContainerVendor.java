package universalcoins.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import universalcoins.tile.TileVendor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerVendor extends Container {
	private TileVendor tileEntity;
	private boolean lastOoStock;
	private boolean lastOoCoins;
	private boolean lastInvFull;
	private int lastCoinSum;
	private int lastUserCoinSum;
	private int lastItemPrice;
	private boolean lastSellMode;
	
	public ContainerVendor(InventoryPlayer inventoryPlayer, TileVendor tEntity) {
		tileEntity = tEntity;
		// the Slot constructor takes the IInventory and the slot number in that
		// it binds to and the x-y coordinates it resides on-screen
		addSlotToContainer(new UCSlotGhost(tileEntity, TileVendor.itemTradeSlot, 9, 17));
		addSlotToContainer(new UCSlotCoinInput(tileEntity, TileVendor.itemCoinInputSlot, 35, 55));
		addSlotToContainer(new UCSlotCard(tileEntity, TileVendor.itemCardSlot, 17, 55));
		addSlotToContainer(new UCSlotOutput(tileEntity, TileVendor.itemCoinOutputSlot, 152, 55));
		
		//add all the inventory storage slots
		for (int i = 0; i < 9; i++) {
				addSlotToContainer(new Slot(tileEntity, TileVendor.itemStorageSlot1 + i, 8 + i * 18, 96));
		}
		
		// commonly used vanilla code that adds the player's inventory
		bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}
	
	void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
						8 + j * 18, 119 + i * 18));
			}
		}
		
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 177));
		}
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);
		// null checks and checks if the item can be stacked (maxStackSize > 1)
		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			
			// merges the item into player inventory since its in the tileEntity
			if (slot < 14) {
				if (!this.mergeItemStack(stackInSlot, 14, 49, true)) {
					return null;
				}
			}
			// places it into the tileEntity is possible since its in the player
			// inventory
			else {
				boolean foundSlot = false;
				for (int i = 1; i < 14; i++){ //we start at 1 to avoid shift clicking into trade slot
					if (((Slot)inventorySlots.get(i)).isItemValid(stackInSlot) && this.mergeItemStack(stackInSlot, i, i + 1, false)) {
						foundSlot = true;
						break;
					}
				}
				if (!foundSlot){
					return null;
				}
			}
			
			if (stackInSlot.stackSize == 0) {
				slotObject.putStack(null);
			}
			else {
				slotObject.onSlotChanged();
			}
			
			if (stackInSlot.stackSize == stack.stackSize) {
				return null;
			}
			slotObject.onPickupFromSlot(player, stackInSlot);
		}
		
		return stack;
	}
	
	/**
     * Looks for changes made in the container, sends them to every listener.
     */
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);
            
            if ( this.lastOoStock != this.tileEntity.ooStockWarning
            		|| this.lastOoCoins != this.tileEntity.ooCoinsWarning
                    || this.lastInvFull != this.tileEntity.inventoryFullWarning
                    || this.lastCoinSum != this.tileEntity.coinSum
            		|| this.lastUserCoinSum != this.tileEntity.userCoinSum
            		|| this.lastItemPrice != this.tileEntity.itemPrice
            		|| this.lastSellMode != this.tileEntity.sellMode) {
                //update
            	tileEntity.updateTE();
            	
            	this.lastOoStock = this.tileEntity.ooStockWarning;
                this.lastOoCoins = this.tileEntity.ooCoinsWarning;
                this.lastInvFull = this.tileEntity.inventoryFullWarning;
        		this.lastCoinSum = this.tileEntity.coinSum;
        		this.lastUserCoinSum = this.tileEntity.userCoinSum;
        		this.lastItemPrice = this.tileEntity.itemPrice;
        		this.lastSellMode = this.tileEntity.sellMode;
            }
        }
	}
	
	@SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 0)
        {
            //this.tileEntity.autoMode = par2;
        }
    }

}
