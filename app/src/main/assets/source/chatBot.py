from qqbot.utf8logger import DEBUG

def onInit(bot):
    print("onInit")

def onQrcode(bot, pngPath, pngContent):
    print("onQrcode: " + pngPath)

def onQQMessage(bot, contact, member, content):
    print("onQQMessage: " + content)
    print("type, contact:%s, member:%s" % (type(contact), type(member)))
    if "hello" in content:
        bot.SendTo(contact, "hello~" + content)
    if contact.nick == ".":
        bot.SendTo(contact, "hahaha")

def onInterval(bot):
    print("onInterval")

def onStartupComplete(bot):
    print("onStartupComplete")

def onUpdate(bot, tinfo):
    print("onUpdate")

def onPlug(bot):
    print("onPlug")

def onUnplug(bot):
    print("onUnplug")

def onExit(bot, code, reason, error):
    DEBUG('%s.onExit: %r %r %r', __name__, code, reason, error)
    
