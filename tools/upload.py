#!/usr/bin/env python3

import os
import requests
import argparse

parser = argparse.ArgumentParser(description="Upload a file.")
parser.add_argument("--file", required=True, help="The path to the file to upload.")
parser.add_argument("--token", required=True, help="The authentication token.")
parser.add_argument("--url", required=True, help="The URL to upload the file to.")

args = parser.parse_args()

def get_upload_url(toUrl: str, token: str):
    response = requests.put(toUrl, headers={
        "Authorization": token,
        "Content-Length": str(0)
    })
    url = response.text
    return url

chunk_size=5*1024*1024

def upload(file_path: str, url: str, token: str):
    with open(file_path, 'rb') as file:
        file.seek(0, os.SEEK_END)
        size = file.tell()
        file.seek(0, os.SEEK_SET)
        link = get_upload_url(url, token)
        if not link:
            print("Failed to get upload link")
            exit(1)
        while True:
            start = file.tell()
            chunk = file.read(chunk_size)
            end = file.tell() - 1
            if not chunk:
                break
            headers = {
                "Content-Type": "application/octet-stream",
                "Content-Length": str(len(chunk)),
                "Content-Range": f"bytes {start}-{end}/{size}",
            }
            requests.put(link, data=chunk, headers=headers)
        
        print(f"File: {file_path}\nSize: {size}Bytes \nURL: {url}")

if __name__ == "__main__":
    upload(args.file, args.url, args.token)