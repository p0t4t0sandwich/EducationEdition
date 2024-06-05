import * as NBT from "nbtify";
import * as fsp from "node:fs/promises";
import { v4 as uuidv4 } from "uuid";

interface Manifest {
  format_version: number;
  header: {
    description: string;
    name: string;
    uuid: string;
    version: [number, number, number];
    min_engine_version: [number, number, number];
  };
  modules: Array<{
    description: string;
    type: string;
    uuid: string;
    version: [number, number, number];
  }>;
}

interface StructureFile {
  structure: {
    entities: Array<{
      Item: {
        tag: {
          display: {
            Name: string;
          };
        };
      };
    }>;
  };
}

function generateManifest(): Manifest {
  return {
    format_version: 2,
    header: {
      description: "AutoGenerated Structure Behavior Pack",
      name: "name_structure_pack",
      uuid: uuidv4(),
      version: [1, 0, 0],
      min_engine_version: [1, 16, 0],
    },
    modules: [
      {
        description: "AutoGenerated Structure Behavior Pack",
        type: "data",
        uuid: uuidv4(),
        version: [1, 0, 0],
      },
    ],
  };
}

async function createManifest(filepath: string) {
  const manifest: Manifest = generateManifest();
  await fsp.writeFile(
    filepath + "/manifest.json",
    JSON.stringify(manifest, null, 2)
  );
}

function getItemName(data: NBT.NBTData): string {
  return (<StructureFile>(<unknown>data.data)).structure.entities[0].Item.tag
    .display.Name;
}

function setItemName(data: NBT.NBTData, name: string): NBT.NBTData {
  (<StructureFile>(
    (<unknown>data.data)
  )).structure.entities[0].Item.tag.display.Name = name;
  return data;
}

async function createNewItem(
  data: NBT.NBTData,
  filepath: string,
  name: string
) {
  data = setItemName(data, name);
  const intArray: Uint8Array = await NBT.write(data);
  await fsp.writeFile(
    filepath + "/structures/" + name + ".mcstructure",
    intArray
  );
}

function nameListProcessing(nameList: string[]): string[] {
  const newList: string[] = [];
  const doubles = new Map<string, string[]>();
  for (const name of nameList) {
    const splitString = name.split(" ");
    const firstName = splitString[0];
    const lastName = splitString[1];
    if (doubles.has(firstName)) {
      const lastNameList = doubles.get(firstName);
      if (lastNameList) {
        lastNameList.push(lastName);
      }
    } else {
      doubles.set(firstName, [lastName]);
    }
  }

  // If there's a double, add the first letter of the last name to the first name
  // Could this horribly die if the children have the same first name and last initial? Probably
  for (const [firstName, lastNameList] of doubles) {
    if (lastNameList.length > 1) {
      for (let i = 0; i < lastNameList.length; i++) {
        const newName = firstName + lastNameList[i][0];
        newList.push(newName);
      }
    } else {
      newList.push(firstName);
    }
  }

  return newList;
}

async function main() {
  if (process.argv.length !== 5) {
    console.error(
      "Usage: bun index.ts <structure file> <name list file> <output fileName>"
    );
    process.exit(1);
  }

  const structureFileName: string = process.argv[2];
  const nameListFileName: string = process.argv[3];
  const outputFileName: string = process.argv[4];

  const nameList: string[] = nameListProcessing(
    (await fsp.readFile(nameListFileName, "utf-8")).split("\n")
  );

  const buffer: Buffer = await fsp.readFile(structureFileName);
  const data: NBT.NBTData = await NBT.read(buffer);

  await fsp.mkdir(outputFileName + "/structures", { recursive: true });
  nameList.forEach(async (name) => {
    await createNewItem(data, outputFileName, name);
  });

  await createManifest(outputFileName);
}

main();
