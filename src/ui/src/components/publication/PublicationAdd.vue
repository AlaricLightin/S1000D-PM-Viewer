<template>
    <v-dialog v-model="dialog" max-width="800px">
        <template v-slot:activator="{ on }">
            <v-btn v-on="on">Добавить</v-btn>
        </template>
        <v-card>
            <v-card-title>
                <span class="headline">Добавить публикацию</span>
            </v-card-title>
            <v-card-text>
                <v-container>
                    <v-file-input label="Выберите XML-файл с публикацией в формате S1000D 4.1"
                                  accept=".xml"
                                  show-size
                                  @change="fileChanged"
                    />
                </v-container>
            </v-card-text>
            <v-card-actions>
                <v-spacer/>
                <v-btn v-bind:disabled="needDisableSubmitButton"
                       v-bind:loading="loading"
                       @click="submitFile()">Загрузить</v-btn>
                <v-btn @click="dialog = false">Отмена</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    // TODO https://stackoverflow.com/questions/40165766/returning-promises-from-vuex-actions
    // TODO написать ограничение по величине загружаемого файла
    export default {
        name: "PublicationAdd",

        data: () => ({
            dialog: false,
            file: null,
            loading: false
        }),

        computed: {
            needDisableSubmitButton: function () {
                return !this.file || this.loading;
            }
        },

        methods: {
            fileChanged(file) {
                this.file = file;
            },

            submitFile() {
                this.loading = true;
                this.$store.dispatch('publications/add', this.file)
                    .then(() => this.dialog = false)
                    // TODO обработать возможные ошибки
                    .catch(() => this.loading = false);
            }
        }
    }
</script>

<style scoped>

</style>