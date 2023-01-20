import sys
import os
from os import listdir
from zeep import Client
cloud = Client("src/main/resources/ServerService.wsdl").service

folder_name = sys.argv[1]
folder_path = "src/main/python/"+folder_name
if not os.path.exists(folder_path):
    sys.exit("Client Folder does not exist!")
backup = input('Name folder you want to create in the Cloud: ')
cloud.create_backup_folder(backup)

while True:
    for name in listdir(folder_path):
        if name == '.DS_Store':
            continue
        file_size = os.path.getsize(folder_path+'/'+name)
        file = os.path.join(folder_path, name)
        if os.path.isfile(file):
            with open(file, 'rb') as f:
                data = f.read()
            f.close()
            cloud.sync(name, file_size, data)
