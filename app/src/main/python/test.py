import os
import time
os.environ['TZ'] = "Asia/Shanghai"
from qqbot import _bot as bot
from qqbot.qqbotcls import QQBot, QQBotSlot, QQBotSched, RunBot, _bot
import qqbotplugins

print(".......")
print(qqbotplugins.__file__)
print(".......")
WORK_PATH = os.path.join(os.path.expanduser('~'), 'tmp')
#PLUGIN_PATH = os.path.dirname(os.path.expanduser('~')) + '/cache/chaquopy/AssetFinder/app.zip' + "/qqbotplugins"
PLUGIN_PATH = "./qqbotplugins"

def run(args):
    print(args)
    del_files(WORK_PATH, "png")
    print("workpath: %s, pluginpath: %s" % (WORK_PATH, PLUGIN_PATH))
    #bot.Login(['-q', '1234', '-b', WORK_PATH, '-pp', PLUGIN_PATH, '-pl', 'chatBot'])
    bot.Login(args)
    bot.Run()
    #bot.Login(['-q', '1234', '-b', WORK_PATH])
    f = bot.List('buddy', 'Nancy')
    while True:
        time.sleep(1000)
        
    if len(f) > 0:
        bot.SendTo(f[0], "hello Nancy")
#os.mkdir('/etc/localtime')

#bot.Login(['-q', '1234'])
    print("hello in run.")

def run_(args):
    RunBot(args)


def del_files(root_dir="C:\temp", file_type="png"):
    """
    @Function: Delete all bmp image file in root_dir and its subdirectory 
    @root_dir: The target directory 
    """
    try:
        file_list = os.listdir(root_dir)
    except:
        return

    for f in file_list:
        file_path = os.path.join(root_dir, f)
        if os.path.isfile(file_path):
            if f.endswith("." + file_type.upper()) or f.endswith("." + file_type.lower()):
                os.remove(file_path)
                print(" File removed! " + file_path)
        elif os.path.isdir(file_path):
            del_files(file_path)

if __name__ == "__main__":
    run(args)
#RunBot(['-q', '1234', '-b', WORK_PATH, '-pp', PLUGIN_PATH, '-pl', 'chatBot'])
