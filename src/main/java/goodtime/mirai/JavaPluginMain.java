package goodtime.mirai;

import goodtime.game.Casinos;
import goodtime.rcon.RconSupport;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public final class JavaPluginMain extends JavaPlugin {

    public static final JavaPluginMain INSTANCE = new JavaPluginMain(); // 可以像 Kotlin 一样静态初始化单例

    private JavaPluginMain() {
        super(new JvmPluginDescriptionBuilder(
                        "com.goodtime.mirai-plugin", // name
                        "1.0.0" // version
                )
                        .author("_JAVA10")
                        .info("A mirai bot for Minecraft command and desk game")
                        .build()
        );
    }

    private static boolean connectSuccessful;

    private static void accept(GroupMessageEvent event) {

        MessageChain message = event.getMessage();
        String membersOut = message.contentToString();

        Casinos.run(event);

        if (membersOut.startsWith("image")) {

            try {
                Contact.sendImage(event.getSubject(), new FileInputStream("src/main/resources/image/board.png"), "png");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


        if (membersOut.startsWith("/")) {

            if (connectSuccessful) {
                String commandReturn = RconSupport.runCommand(membersOut, event.getSender().getNameCard());
                if (membersOut.contains("/list")) {
                    event.getSubject().sendMessage(commandReturn);
                } else if (commandReturn != null) {
                    event.getSubject().sendMessage(commandReturn);
                } else {
                    event.getSubject().sendMessage("宁没有权限使用此命令或命令错误。");
                }
            } else {
                event.getSubject().sendMessage("连接Minecraft服务器失败，服务器未开启或检查Rcon配置文件是否正确。");
            }

        }
        if (membersOut.startsWith("./")) {
            if (membersOut.contains("./help")) {
                event.getSubject().sendMessage("欢迎使用GoodTime智慧型机器人：您现可使用以下指令：" +
                        "\nMinecraft指令：" +
                        "\n/list" +
                        "\n/say [text]" +
                        "\n/forge tps\n" +
                        "\n其他指令：" +
                        "复读 [text]\n");
            }

            if (membersOut.contains("./game 斗地主")) {
                event.getSubject().sendMessage("斗地主指令：\n" +
                        "\n上桌指令：[.上桌], [.fork table], [.sz], [斗地主]\n" +
                        "积分指令：[我的积分]");
            }
        }

        if (membersOut.startsWith("复读 ")) {

            for (SingleMessage singleMessage : message) {
                if (singleMessage instanceof Image) {
                    event.getSubject().sendMessage(singleMessage);
                } else if (singleMessage instanceof PlainText) {
                    event.getSubject().sendMessage(singleMessage.contentToString().replace("复读 ", ""));
                }
            }

        }

        if (membersOut.equals("我的积分")) {
            long id = event.getSender().getId();
            //如果从json里读取不到这个玩家吗，就把这个玩家丢进去，给一个默认积分
            Casinos.SCORE_MAP.putIfAbsent(event.getSender().getId(), 5000);
            //存入json
            Casinos.scoreJson.save();
            event.getSubject().sendMessage(new At(id).plus("你的积分：" + Casinos.SCORE_MAP.get(id)));
        }
    }

    @Override
    public void onEnable() {
        Listener l = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, JavaPluginMain::accept);
       /*
       connectSuccessful = RconSupport.connectToServer();
        if (connectSuccessful) {
            getLogger().info("Minecraft服务器连接成功");
        }else {
            getLogger().info("Minecraft服务器连接失败");
        }*/
    }

}
