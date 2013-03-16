#-------------------------------------------------------------------------------
# Name:        ssh.py
# Description: starts the metadata and data servers using metadata-config.txt
#-------------------------------------------------------------------------------
from subprocess import call

'''
starts the metadata server and data servers
'''
def start_servers(metaserver, dataservers_list):
    # print the commands that need to be executed (replace these later)
    print "ssh", metaserver, "~hallwort/start_metaserver"
    for s in dataservers_list:
        print "ssh", s, "~hallwort/public/start_dataserver"
        
    # only replace the above print with something like:
    # (need to add some java files to my public folder to run)
    # call(["ssh", server, "java ~hallwort/public/DataServer"])
    # or find a good ssh lib to use
	# the call command just executes that statement in the terminal with the specified args

'''
reads the config file, calls start_servers with the name of metadata server
and a list of the data servers to be started
'''
def main():
    # reads config.txt to get server names. it needs to be in same directory as this file
    f = open('metadata-config.txt', mode='r')
    metaserver = f.readline()
    dataservers = f.readline()

    # remove all whitespace characters (newlines, spaces, etc.)
    metaserver = "".join(metaserver.split())
    dataservers = "".join(dataservers.split())

    # only take after the equals sign
    metaserver = metaserver[metaserver.find("=")+1:]
    dataservers = dataservers[dataservers.find("=")+1:]
    dataservers_list = dataservers.split(",")
    
    start_servers(metaserver, dataservers_list)

if __name__ == '__main__':
    main()
