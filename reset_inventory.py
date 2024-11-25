import psycopg2
import os

# Configuración de la conexión a PostgreSQL
DB_CONFIG = {
    "dbname": "cinema_db",
    "user": "cinema_user",
    "password": "cinema_pass",
    "host": "cinema-postgres",  # Nombre del contenedor PostgreSQL
    "port": 5432,
}

# Ruta del archivo de texto
FOOD_FILE = os.getenv("PATH_FILE", "/app/data/food.txt")

# Valores iniciales del inventario
DEFAULT_INVENTORY = [
    {"producto": "Nachos", "cantidad": 30},
    {"producto": "Palomitas", "cantidad": 50},
    {"producto": "Refresco", "cantidad": 100},
]

def reset_inventory():
    try:
        # Reiniciar la base de datos
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()

        for item in DEFAULT_INVENTORY:
            cursor.execute(
                """
                UPDATE comidas
                SET cantidad = %s
                WHERE producto = %s;
                """,
                (item["cantidad"], item["producto"]),
            )

        conn.commit()
        cursor.close()
        conn.close()
        print("Base de datos reiniciada.")

        # Reiniciar el archivo de texto
        with open(FOOD_FILE, "w") as file:
            for item in DEFAULT_INVENTORY:
                file.write(f"{item['producto']},{item['cantidad']}\n")

        print(f"Archivo {FOOD_FILE} reiniciado.")

    except Exception as e:
        print(f"Error al reiniciar el inventario: {e}")

if __name__ == "__main__":
    reset_inventory()

