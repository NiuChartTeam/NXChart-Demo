#!/usr/bin/env python

#---------
# Script to change the prefix of a framework
#---------
import codecs
import json
import os
import re
import string

# Root directories
rootDir = os.path.join(os.path.dirname(__file__) + '/../main/assets/report/')
file_encodeing='utf-8'
#------------------
# format json
#------------------

def treatDirectory(arg, dirName, names):
    for fileName in names:
        if fileName.endswith(('.json','.nx')):
            print "reading file Name: %s" % fileName
            file_location=dirName+"/"+fileName

            #read
            file_stream = codecs.open(file_location, 'r', file_encodeing)
            jsonString = json.load(file_stream,encoding=file_encodeing)

            parsedString=json.dumps(jsonString, indent=4, sort_keys=False,ensure_ascii=False)
            file_stream.close()

            #print parsedString

            #write
            file_stream = codecs.open(file_location, 'w', file_encodeing)
            file_stream.write(parsedString)
            file_stream.close()




# Walk over all files in original directory
os.path.walk(rootDir, treatDirectory, None)