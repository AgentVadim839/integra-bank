import subprocess
import requests
import json
import sys

def get_last_commit_diff():
    try:
        result = subprocess.run(
            ["git", "show", "HEAD", "--unified=0"],
            capture_output=True,
            text = True,
            encoding="utf-8"
        )
        if result.returncode != 0:
            print("Ошибка Git:", result.stderr)
            return None
        return result.stdout
    except:
        print("Ошибка: Git не найден. Убедись, что он установлен.")
        return None

if __name__ == "__main__":
    diff = get_last_commit_diff()
    if diff:
        print("Чото вышло, длина:", len(diff))
        print(diff[:100])