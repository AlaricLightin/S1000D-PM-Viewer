<template>
    <div>
        <PublicationList/>
        <PublicationAdd/>
    </div>
</template>

<script>
    import PublicationList from "./PublicationList";
    import PublicationAdd from "./PublicationAdd";

    export default {
        name: "Publications",
        components: {PublicationList, PublicationAdd}
    }
</script>

<style scoped>

</style>