import json
import requests


def run():
    lic_file = open("..\\core\\assets\\config\\license.json", "r")
    lic_buf = lic_file.read()
    lic_file.close()
    lic_dic = json.loads(lic_buf)
    license = lic_dic["licenseKey"]
    unique_id = lic_dic["uniqueId"]
    url = "http://f15slic.franklinpracticum.com/api/license_response.php?response=verify&license={0}&id={1}".format(
        license, unique_id)
    r = requests.post(url)
    response = r.text

    print r.text


if __name__ == "__main__":
    run()
