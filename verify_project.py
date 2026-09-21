from pathlib import Path
import zipfile

ROOT = Path(__file__).parent
errors=[]
required = [
    ROOT/'build.gradle',
    ROOT/'src/main/resources/mcmod.info',
    ROOT/'libs/cfm-6.3.2-1.12.2.jar',
    ROOT/'src/main/java/com/example/furniturepower/FurniturePower.java',
    ROOT/'src/main/java/com/example/furniturepower/event/PowerEvents.java',
]
for p in required:
    if not p.exists(): errors.append(f'missing: {p}')
with zipfile.ZipFile(ROOT/'libs/cfm-6.3.2-1.12.2.jar') as z:
    names=set(z.namelist())
    for p in [
        'com/mrcrayfish/furniture/tileentity/TileEntityTV.class',
        'com/mrcrayfish/furniture/tileentity/TileEntityMicrowave.class',
        'com/mrcrayfish/furniture/tileentity/TileEntityComputer.class',
        'com/mrcrayfish/furniture/tileentity/TileEntityWashingMachine.class',
    ]:
        if p not in names: errors.append('CFM missing: '+p)
print('OK' if not errors else '\n'.join(errors))
raise SystemExit(1 if errors else 0)
