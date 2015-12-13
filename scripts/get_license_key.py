import json
import os
import requests
import xml.etree.ElementTree as ET


def run():
    url = "http://f15slic.franklinpracticum.com/api/license_response.php?response=activate&license={0}"
    # key = os.urandom(16).encode('base64').strip()
    key = "K8LEE-R37L9-8ESN9-OBH2V"
    r = requests.post(url.format(key))
    print "result: " + r.text
    xml_text = r.text
    # soup = BeautifulSoup(xml_text, "xml")
    root = ET.fromstring(xml_text)
    license_key = root.findall("./licenseKey")[0].text
    expiration_date = root.findall("./expirationDate")[0].text
    unique_id = root.findall("./uniqueId")[0].text

    license = {'licenseKey': license_key, 'expirationDate': expiration_date,
               'uniqueId': unique_id}
    license_json = json.dumps(license)
    cwd = os.getcwd()
    print "cwd: {0}".format(cwd)
    path = cwd.strip("scripts")
    print "path: {0}".format(path)
    path = path + "core\\assets\\config\\license.json"
    print "path: {0}".format(path)
    license_file = open(path, "w+")
    license_file.write(license_json)
    license_file.close()
    print "finished"


if __name__ == '__main__':
    run()
