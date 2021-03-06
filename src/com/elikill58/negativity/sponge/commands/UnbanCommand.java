package com.elikill58.negativity.sponge.commands;

import static org.spongepowered.api.command.args.GenericArguments.onlyOne;
import static org.spongepowered.api.command.args.GenericArguments.user;

import java.util.List;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.utils.NegativityCmdWrapper;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.BanRequest;

public class UnbanCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		User target = args.requireOne("target");

		NegativityAccount targetAccount = Adapter.getAdapter().getNegativityAccount(target.getUniqueId());
		List<BanRequest> banRequests = targetAccount.getBanRequest();
		for (BanRequest banRequest : banRequests) {
			banRequest.unban();
		}

		Messages.sendMessage(src, "unban.well_unban", "%name%", target.getName());
		return CommandResult.successCount(banRequests.size());
	}

	public static CommandCallable create() {
		CommandSpec command = CommandSpec.builder()
				.executor(new UnbanCommand())
				.arguments(onlyOne(user(Text.of("target"))))
				.build();
		return new NegativityCmdWrapper(command, false, "unban");
	}
}
